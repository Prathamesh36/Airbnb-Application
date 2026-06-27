package com.portfolio.projects.paymentservice.service.impl;

import com.portfolio.projects.paymentservice.dto.CheckoutRequest;
import com.portfolio.projects.paymentservice.entity.Payment;
import com.portfolio.projects.paymentservice.entity.enums.PaymentStatus;
import com.portfolio.projects.paymentservice.repository.PaymentRepository;
import com.portfolio.projects.paymentservice.service.CheckoutService;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckoutServiceImpl implements CheckoutService {

    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public String getCheckoutSession(CheckoutRequest checkoutRequest, String successUrl, String failureUrl) {
        log.info("Creating session for booking with ID: {}", checkoutRequest.getBookingId());

        try {
            CustomerCreateParams customerParams = CustomerCreateParams.builder()
                    .setName(checkoutRequest.getUserName())
                    .setEmail(checkoutRequest.getUserEmail())
                    .build();
            Customer customer = Customer.create(customerParams);

            SessionCreateParams sessionParams = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setBillingAddressCollection(SessionCreateParams.BillingAddressCollection.REQUIRED)
                    .setCustomer(customer.getId())
                    .setSuccessUrl(successUrl)
                    .setCancelUrl(failureUrl)
                    .setClientReferenceId(String.valueOf(checkoutRequest.getBookingId()))
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("inr")
                                                    .setUnitAmount(checkoutRequest.getAmount().multiply(BigDecimal.valueOf(100)).longValue())
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName(checkoutRequest.getPropertyName() +" : "+ checkoutRequest.getRoomType())
                                                                    .setDescription("Booking ID: "+checkoutRequest.getBookingId())
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();

            Session session = Session.create(sessionParams);

            // Create initial payment record
            Payment payment = new Payment();
            payment.setBookingId(checkoutRequest.getBookingId());
            payment.setAmount(checkoutRequest.getAmount());
            payment.setTransactionId(session.getId());
            payment.setPaymentStatus(PaymentStatus.PENDING);
            paymentRepository.save(payment);

            log.info("Session created successfully for booking with ID: {}", checkoutRequest.getBookingId());
            return session.getUrl();

        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }
}
