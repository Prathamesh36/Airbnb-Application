package com.portfolio.projects.paymentservice.service;

import com.portfolio.projects.common.dto.CheckoutRequest;

public interface CheckoutService {
    String getCheckoutSession(CheckoutRequest checkoutRequest, String successUrl, String failureUrl);
}
