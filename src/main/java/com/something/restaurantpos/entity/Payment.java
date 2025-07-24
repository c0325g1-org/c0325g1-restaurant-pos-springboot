package com.something.restaurantpos.entity;

import com.something.restaurantpos.entity.base.AuditMetadata;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment extends AuditMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private Invoice invoice;

    @Enumerated(EnumType.STRING)
    private Method method;

    private BigDecimal amount;
    private LocalDateTime paymentTime;

    public enum Method {
        CASH, TRANSFER, VNPAY
    }
}
