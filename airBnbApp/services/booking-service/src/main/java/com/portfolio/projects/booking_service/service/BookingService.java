package com.portfolio.projects.booking_service.service;

import com.portfolio.projects.booking_service.dto.BookingDto;
import com.portfolio.projects.booking_service.dto.BookingRequest;
import com.portfolio.projects.booking_service.dto.GuestDto;
import com.portfolio.projects.booking_service.dto.PropertyReportDto;
import com.stripe.model.Event;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {

    BookingDto initialiseBooking(BookingRequest bookingRequest);

    BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList);

    String initiatePayments(Long bookingId);

    void capturePayment(Event event);

    void cancelBooking(Long bookingId);

    String getBookingStatus(Long bookingId);

    List<BookingDto> getAllBookingsByPropertyId(Long PropertyId);

    PropertyReportDto getPropertyReport(Long PropertyId, LocalDate startDate, LocalDate endDate);

    List<BookingDto> getMyBookings();
}

