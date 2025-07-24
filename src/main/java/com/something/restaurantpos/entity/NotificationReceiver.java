package com.something.restaurantpos.entity;

import com.something.restaurantpos.entity.base.AuditMetadata;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_receivers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationReceiver extends AuditMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private Notification notification;

    @ManyToOne
    private Employee employee;

    private Boolean isRead = false;
    private LocalDateTime readAt;
}
