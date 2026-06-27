package com.portfolio.projects.booking_service.service.impl;

import com.portfolio.projects.booking_service.dto.BookingDto;
import com.portfolio.projects.booking_service.dto.BookingRequest;
import com.portfolio.projects.booking_service.dto.GuestDto;
import com.portfolio.projects.booking_service.dto.PropertyReportDto;
import com.portfolio.projects.booking_service.entity.*;
import com.portfolio.projects.booking_service.entity.enums.BookingStatus;
import com.portfolio.projects.booking_service.exception.ResourceNotFoundException;
import com.portfolio.projects.booking_service.exception.UnAuthorisedException;
import com.portfolio.projects.booking_service.repository.*;
import com.portfolio.projects.booking_service.client.PropertyClient;
import com.portfolio.projects.booking_service.client.dto.PropertyDto;
import com.portfolio.projects.booking_service.client.dto.RoomDto;

import com.portfolio.projects.booking_service.service.BookingService;
import com.portfolio.projects.booking_service.service.CheckoutService;
import com.portfolio.projects.booking_service.client.InventoryClient;
import com.portfolio.projects.booking_service.client.dto.InventoryBookingDto;
import com.portfolio.projects.booking_service.client.dto.ReserveInventoryResponse;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.RefundCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.portfolio.projects.booking_service.util.AppUtils.getCurrentUserId;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService{
    private final GuestRepository guestRepository;
    private final ModelMapper modelMapper;

    private final BookingRepository bookingRepository;
    private final CheckoutService checkoutService;
    private final InventoryClient inventoryClient;
    private final PropertyClient propertyClient;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    @Transactional
    public BookingDto initialiseBooking(BookingRequest bookingRequest) {

        log.info("Initialising booking for Property : {}, room: {}, date {}-{}", bookingRequest.getPropertyId(),
                bookingRequest.getRoomId(), bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate());

        PropertyDto property = propertyClient.getPropertyById(bookingRequest.getPropertyId());
        RoomDto room = propertyClient.getRoomById(bookingRequest.getRoomId());

        InventoryBookingDto inventoryBookingDto = InventoryBookingDto.builder()
                .roomId(room.getId())
                .checkInDate(bookingRequest.getCheckInDate())
                .checkOutDate(bookingRequest.getCheckOutDate())
                .roomsCount(bookingRequest.getRoomsCount())
                .build();
                
        ReserveInventoryResponse response = inventoryClient.reserveInventory(inventoryBookingDto);
        BigDecimal totalPrice = response.getTotalPrice();

        Booking booking = Booking.builder()
                .bookingStatus(BookingStatus.RESERVED)
                .propertyId(property.getId())
                .roomId(room.getId())
                .checkInDate(bookingRequest.getCheckInDate())
                .checkOutDate(bookingRequest.getCheckOutDate())
                .userId(getCurrentUserId())
                .roomsCount(bookingRequest.getRoomsCount())
                .amount(totalPrice)
                .build();

        booking = bookingRepository.save(booking);
        return modelMapper.map(booking, BookingDto.class);
    }

    @Override
    @Transactional
    public BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList) {

        log.info("Adding guests for booking with id: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new ResourceNotFoundException("Booking not found with id: "+bookingId));
        Long userId = getCurrentUserId();

        if (!userId.equals(booking.getUserId())) {
            throw new UnAuthorisedException("Booking does not belong to this user with id: "+userId);
        }

        if (hasBookingExpired(booking)) {
            throw new IllegalStateException("Booking has already expired");
        }

        if(booking.getBookingStatus() != BookingStatus.RESERVED) {
            throw new IllegalStateException("Booking is not under reserved state, cannot add guests");
        }

        for (GuestDto guestDto: guestDtoList) {
            Guest guest = modelMapper.map(guestDto, Guest.class);
            guest.setUserId(userId);
            guest = guestRepository.save(guest);
            booking.getGuests().add(guest);
        }

        booking.setBookingStatus(BookingStatus.GUESTS_ADDED);
        booking = bookingRepository.save(booking);
        return modelMapper.map(booking, BookingDto.class);
    }

    @Override
    @Transactional
    public String initiatePayments(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new ResourceNotFoundException("Booking not found with id: "+bookingId)
        );
        Long userId = getCurrentUserId();
        if (!userId.equals(booking.getUserId())) {
            throw new UnAuthorisedException("Booking does not belong to this user with id: "+userId);
        }
        if (hasBookingExpired(booking)) {
            throw new IllegalStateException("Booking has already expired");
        }

        String sessionUrl = checkoutService.getCheckoutSession(booking,
                frontendUrl+"/payments/success", frontendUrl+"/payments/failure");

        booking.setBookingStatus(BookingStatus.PAYMENTS_PENDING);
        bookingRepository.save(booking);

        return sessionUrl;
    }

    @Override
    @Transactional
    public void capturePayment(Event event) {
        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
            if (session == null) return;

            String sessionId = session.getId();
            Booking booking =
                    bookingRepository.findByPaymentSessionId(sessionId).orElseThrow(() ->
                            new ResourceNotFoundException("Booking not found for session ID: "+sessionId));

            booking.setBookingStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);

            InventoryBookingDto inventoryBookingDto = InventoryBookingDto.builder()
                    .roomId(booking.getRoomId())
                    .checkInDate(booking.getCheckInDate())
                    .checkOutDate(booking.getCheckOutDate())
                    .roomsCount(booking.getRoomsCount())
                    .build();
            inventoryClient.confirmInventory(inventoryBookingDto);

            log.info("Successfully confirmed the booking for Booking ID: {}", booking.getId());
        } else {
            log.warn("Unhandled event type: {}", event.getType());
        }
    }

    @Override
    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new ResourceNotFoundException("Booking not found with id: "+bookingId)
        );
        Long userId = getCurrentUserId();
        if (!userId.equals(booking.getUserId())) {
            throw new UnAuthorisedException("Booking does not belong to this user with id: "+userId);
        }

        if(booking.getBookingStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Only confirmed bookings can be cancelled");
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        InventoryBookingDto inventoryBookingDto = InventoryBookingDto.builder()
                .roomId(booking.getRoomId())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .roomsCount(booking.getRoomsCount())
                .build();
        inventoryClient.releaseInventory(inventoryBookingDto);

        // handle the refund
        try {
            Session session = Session.retrieve(booking.getPaymentSessionId());
            RefundCreateParams refundParams = RefundCreateParams.builder()
                    .setPaymentIntent(session.getPaymentIntent())
                    .build();

            Refund.create(refundParams);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBookingStatus(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new ResourceNotFoundException("Booking not found with id: "+bookingId)
        );
        Long userId = getCurrentUserId();
        if (!userId.equals(booking.getUserId())) {
            throw new UnAuthorisedException("Booking does not belong to this user with id: "+userId);
        }

        return booking.getBookingStatus().name();
    }

    @Override
    public List<BookingDto> getAllBookingsByPropertyId(Long propertyId) {
        PropertyDto property = propertyClient.getPropertyById(propertyId);
        Long userId = getCurrentUserId();

        log.info("Getting all booking for the Property with ID: {}", propertyId);

        if(!userId.equals(property.getOwnerId())) throw new AccessDeniedException("You are not the owner of Property with id: "+propertyId);

        List<Booking> bookings = bookingRepository.findByPropertyId(propertyId);

        return bookings.stream()
                .map((element) -> modelMapper.map(element, BookingDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public PropertyReportDto getPropertyReport(Long propertyId, LocalDate startDate, LocalDate endDate) {
        PropertyDto property = propertyClient.getPropertyById(propertyId);
        Long userId = getCurrentUserId();

        log.info("Generating report for Property with ID: {}", propertyId);

        if(!userId.equals(property.getOwnerId())) throw new AccessDeniedException("You are not the owner of Property with id: "+propertyId);

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<Booking> bookings = bookingRepository.findByPropertyIdAndCreatedAtBetween(propertyId, startDateTime, endDateTime);

        Long totalConfirmedBookings = bookings
                .stream()
                .filter(booking -> booking.getBookingStatus() == BookingStatus.CONFIRMED)
                .count();

        BigDecimal totalRevenueOfConfirmedBookings = bookings.stream()
                .filter(booking -> booking.getBookingStatus() == BookingStatus.CONFIRMED)
                .map(Booking::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal avgRevenue = totalConfirmedBookings == 0L ? BigDecimal.ZERO :
                totalRevenueOfConfirmedBookings.divide(BigDecimal.valueOf(totalConfirmedBookings), RoundingMode.HALF_UP);

        return new PropertyReportDto(totalConfirmedBookings, totalRevenueOfConfirmedBookings, avgRevenue);
    }

    @Override
    public List<BookingDto> getMyBookings() {
        Long userId = getCurrentUserId();

        return bookingRepository.findByUserId(userId)
                .stream().
                map((element) -> modelMapper.map(element, BookingDto.class))
                .collect(Collectors.toList());
    }

    public boolean hasBookingExpired(Booking booking) {
        return booking.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now());
    }
}
