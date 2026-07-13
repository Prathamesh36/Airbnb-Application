package com.portfolio.projects.booking_service.service;

import com.portfolio.projects.common.dto.BookingDto;
import com.portfolio.projects.common.dto.BookingRequest;
import com.portfolio.projects.common.dto.GuestDto;
import com.portfolio.projects.common.dto.PropertyReportDto;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {

    BookingDto initialiseBooking(BookingRequest bookingRequest);

    BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList);

    String initiatePayments(Long bookingId);


    void cancelBooking(Long bookingId);

    String getBookingStatus(Long bookingId);

    List<BookingDto> getAllBookingsByPropertyId(Long propertyId);

    PropertyReportDto getPropertyReport(Long propertyId, LocalDate startDate, LocalDate endDate);

    List<BookingDto> getMyBookings();
}

