package com.something.restaurantpos.entity;

import com.something.restaurantpos.entity.base.AuditMetadata;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reservation extends AuditMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String customerName;
    private String customerPhone;

    @ManyToOne
    @JoinColumn(name = "table_id")
    private DiningTable table;

    private LocalDateTime reservationTime;

    @Column(columnDefinition = "TEXT")
    private String note;
}
