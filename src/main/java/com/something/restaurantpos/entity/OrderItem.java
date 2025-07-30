package com.something.restaurantpos.entity;

import com.something.restaurantpos.entity.base.AuditMetadata;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem extends AuditMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private Order order;

    @ManyToOne

    private MenuItem menuItem;

    private Integer quantity;
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private ItemStatus status = ItemStatus.NEW;

    public enum ItemStatus {
        NEW, COOKING, READY, SERVED,CANCELED;
    }
}
