package com.portfolio.projects.paymentservice.service;

import com.portfolio.projects.paymentservice.entity.Payment;
import com.portfolio.projects.common.enums.PaymentStatus;
import com.portfolio.projects.paymentservice.repository.PaymentRepository;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, Long> kafkaTemplate;

    @Transactional
    public void capturePayment(Event event) {
        if ("checkout.session.completed".equals(event.getType())) {
            Session session = null;
            if (event.getDataObjectDeserializer().getObject().isPresent()) {
                session = (Session) event.getDataObjectDeserializer().getObject().get();
            } else {
                try {
                    session = (Session) event.getDataObjectDeserializer().deserializeUnsafe();
                } catch (Exception e) {
                    log.error("Error deserializing Stripe session unsafely", e);
                }
            }

            if (session == null) return;

            String sessionId = session.getId();
            log.info("Payment session completed: {}", sessionId);

            // Since we don't have sessionId in Payment yet, we might need to store it or extract bookingId from metadata
            // Let's assume bookingId was stored in client_reference_id
            String clientReferenceId = session.getClientReferenceId();
            if (clientReferenceId != null) {
                Long bookingId = Long.parseLong(clientReferenceId);
                
                Payment payment = paymentRepository.findByBookingId(bookingId)
                        .orElseThrow(() -> new RuntimeException("Payment not found for booking: " + bookingId));
                
                payment.setPaymentStatus(PaymentStatus.SUCCESS);
                paymentRepository.save(payment);

                // Publish Event
                kafkaTemplate.send("payment-completed-topic", bookingId);
                log.info("Published payment-completed-topic for bookingId: {}", bookingId);
            }
        }
    }

    @Transactional
    public void refundPayment(Long bookingId) throws com.stripe.exception.StripeException {
        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Payment not found for booking: " + bookingId));

        if (payment.getPaymentStatus() == PaymentStatus.REFUNDED) {
            log.info("Payment already refunded for bookingId: {}", bookingId);
            return;
        }

        String sessionId = payment.getTransactionId();
        Session session = Session.retrieve(sessionId);
        String paymentIntentId = session.getPaymentIntent();

        if (paymentIntentId != null) {
            com.stripe.param.RefundCreateParams params = com.stripe.param.RefundCreateParams.builder()
                    .setPaymentIntent(paymentIntentId)
                    .build();
            com.stripe.model.Refund refund = com.stripe.model.Refund.create(params);

            payment.setPaymentStatus(PaymentStatus.REFUNDED);
            paymentRepository.save(payment);
            log.info("Successfully refunded payment for bookingId: {}, refundId: {}", bookingId, refund.getId());
        } else {
            log.error("PaymentIntent is null for session: {}. Cannot issue refund.", sessionId);
        }
    }
}
