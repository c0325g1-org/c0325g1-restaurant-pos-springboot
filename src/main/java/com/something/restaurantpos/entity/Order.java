package com.something.restaurantpos.entity;

import com.something.restaurantpos.entity.base.AuditMetadata;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

    @ManyToOne
    private Reservation reservation;

    private LocalDateTime orderTime;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.OPEN;

    public enum OrderStatus {
        OPEN, CLOSED
    }
}
