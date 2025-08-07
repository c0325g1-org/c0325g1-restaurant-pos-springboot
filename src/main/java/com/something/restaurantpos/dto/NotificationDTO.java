package com.something.restaurantpos.dto;

import com.something.restaurantpos.entity.Employee;
import com.something.restaurantpos.entity.Notification;
import com.something.restaurantpos.util.CurrentUserUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
public class NotificationDTO {
    private Integer notificationReceiverId;
    private String message;
    private String senderName;
    private String senderRole;
    private LocalDateTime createdAt;
    private Notification.NotificationType notificationType;
    private boolean read;

    public NotificationDTO() {
        Employee employee = CurrentUserUtil.getCurrentEmployee();
        this.notificationType = Notification.NotificationType.INFO;
        this.read = false;
        if (employee != null) {
            this.senderName = Objects.requireNonNull(employee).getName();
            this.senderRole = Objects.requireNonNull(employee).getRole().getName();
        } else {
            this.senderName = "Hệ thống";
            this.senderRole = "";
        }
        this.createdAt = LocalDateTime.now();
    }
}
