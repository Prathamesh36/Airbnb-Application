package com.portfolio.projects.paymentservice.controller;

import com.portfolio.projects.paymentservice.dto.CheckoutRequest;
import com.portfolio.projects.paymentservice.service.CheckoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/internal/payments")
@RequiredArgsConstructor
public class InternalPaymentController {

    private final CheckoutService checkoutService;

    @PostMapping("/checkout")
    public ResponseEntity<Map<String, String>> initiateCheckout(@RequestBody CheckoutRequest checkoutRequest) {
        // Hardcoded success/failure URLs for now, or could pass via headers/properties
        String successUrl = "http://localhost:8000/payment/success";
        String failureUrl = "http://localhost:8000/payment/failure";
        String sessionUrl = checkoutService.getCheckoutSession(checkoutRequest, successUrl, failureUrl);
        return ResponseEntity.ok(Map.of("sessionUrl", sessionUrl));
    }

    @PostMapping("/refund")
    public ResponseEntity<Void> refundPayment(@RequestBody Map<String, Long> request) {
        Long bookingId = request.get("bookingId");
        // For now just logging it. Full Stripe Refund logic can be added later.
        System.out.println("Processing refund for booking ID: " + bookingId);
        return ResponseEntity.noContent().build();
    }
}
