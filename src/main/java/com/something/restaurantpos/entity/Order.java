package com.something.restaurantpos.entity;

import com.something.restaurantpos.entity.base.AuditMetadata;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order extends AuditMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private DiningTable table;

    @ManyToOne
    private Employee employee;

    private LocalDateTime orderTime;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.OPEN;

    @Column(nullable = false, unique = true, updatable = false, length = 36)
    private String feedbackToken;

    @PrePersist
    public void generateFeedbackToken() {
        if (feedbackToken == null) {
            feedbackToken = UUID.randomUUID().toString();
        }
    }

    public enum OrderStatus {
        OPEN, CLOSED, CANCELED
    }
}
