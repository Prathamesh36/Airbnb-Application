package com.portfolio.projects.booking_service.service;


import com.portfolio.projects.booking_service.entity.Booking;

public interface CheckoutService {

    String getCheckoutSession(Booking booking, String successUrl, String failureUrl);

}
