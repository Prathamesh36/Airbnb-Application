package com.portfolio.projects.booking_service.client.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class CheckoutRequest {
    private Long bookingId;
    private Long propertyId;
    private Long roomId;
    private Long userId;
    private String userName;
    private String userEmail;
    private BigDecimal amount;
    private String propertyName;
    private String roomType;
}
