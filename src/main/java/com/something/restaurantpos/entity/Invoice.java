package com.something.restaurantpos.entity;

import com.something.restaurantpos.entity.base.AuditMetadata;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "invoices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Invoice extends AuditMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @OneToOne
    @JoinColumn(name = "order_id", unique = true)
    private Order order;
    @ManyToOne
    private Voucher voucher;
    private BigDecimal totalAmount;
    @Column(name = "paid")
    private boolean paid = false;
    @Column(columnDefinition = "TEXT")
    private String note;
    @ManyToOne
    @JoinColumn(name = "table_id")
    private DiningTable diningTable;
}
