package com.portfolio.projects.booking_service.client;

import com.portfolio.projects.booking_service.client.dto.CheckoutRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "payment-service", url = "${payment.service.url:http://localhost:8086}")
public interface PaymentClient {
    @PostMapping("/internal/payments/checkout")
    Map<String, String> initiateCheckout(@RequestBody CheckoutRequest checkoutRequest);

    @PostMapping("/internal/payments/refund")
    void refundPayment(@RequestBody Map<String, Long> request);
}
