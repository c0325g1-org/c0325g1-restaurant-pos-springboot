package com.something.restaurantpos.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class PaymentDto {
    private Integer invoiceId;
    private BigDecimal amount;
    private String method;// "CASH", "TRANSFER", "VNPAY"
    private LocalDateTime paymentTime;
}