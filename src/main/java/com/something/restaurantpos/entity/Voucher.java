package com.something.restaurantpos.entity;

import com.something.restaurantpos.entity.base.AuditMetadata;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "vouchers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Voucher extends AuditMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String code;
    @Column(columnDefinition = "TEXT")
    private String description;
    private Integer discountPercent;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private Boolean isActive = true;
}
