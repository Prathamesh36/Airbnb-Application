package com.portfolio.projects.paymentservice.service;

import com.portfolio.projects.paymentservice.entity.Payment;
import com.portfolio.projects.paymentservice.entity.enums.PaymentStatus;
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
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
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
}
