package com.portfolio.projects.airBnbApp.service;


import com.portfolio.projects.airBnbApp.entity.Booking;

public interface CheckoutService {

    String getCheckoutSession(Booking booking, String successUrl, String failureUrl);

}
