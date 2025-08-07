package com.something.restaurantpos.entity;

import com.something.restaurantpos.entity.base.AuditMetadata;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role extends AuditMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    public enum UserRole {
        ROLE_WAITER, ROLE_KITCHEN, ROLE_CASHIER, ROLE_MANAGER, ROLE_ADMIN, ROLE_AGENT
    }
}
