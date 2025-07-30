package com.something.restaurantpos.entity;

import com.something.restaurantpos.entity.base.AuditMetadata;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
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

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;

    public enum OrderStatus {
        OPEN, CLOSED, CANCELED
    }
}
