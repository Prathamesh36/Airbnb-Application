package com.portfolio.projects.airBnbApp.service;

import com.portfolio.projects.airBnbApp.dto.BookingDto;
import com.portfolio.projects.airBnbApp.dto.BookingRequest;
import com.portfolio.projects.airBnbApp.dto.GuestDto;

import java.util.List;

public interface BookingService {

    BookingDto initialiseBooking(BookingRequest bookingRequest);

    BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList);
}
