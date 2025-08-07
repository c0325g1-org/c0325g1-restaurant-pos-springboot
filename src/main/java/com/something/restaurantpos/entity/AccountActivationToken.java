package com.something.restaurantpos.entity;

import com.something.restaurantpos.entity.base.AuditMetadata;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "account_activation_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountActivationToken extends AuditMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 255)
    private String token;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @Column(nullable = false)
    private Boolean used = false;

    @PrePersist
    public void generateToken() {
        if (token == null) {
            this.token = java.util.UUID.randomUUID().toString();
        }
        if (expiryDate == null) {
            // Token có hiệu lực trong 24 giờ
            this.expiryDate = LocalDateTime.now().plusHours(24);
        }
    }
} 