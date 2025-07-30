package com.something.restaurantpos.entity;

import com.something.restaurantpos.entity.base.AuditMetadata;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DialectOverride;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "deleted = false")
public class Order extends AuditMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private DiningTable table;

    @ManyToOne
    private Employee employee;

    @Column(nullable = false, unique = true, updatable = false, length = 36)
    private String feedbackToken;

    @PrePersist
    public void generateFeedbackToken() {
        if (feedbackToken == null) {
            feedbackToken = UUID.randomUUID().toString();
        }
    }


    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.OPEN;

    public enum OrderStatus {
        OPEN, CLOSED, CANCELED;
    }
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<OrderItem> items = new ArrayList<>();

}
