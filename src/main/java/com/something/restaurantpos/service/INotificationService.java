package com.something.restaurantpos.service;

import com.something.restaurantpos.dto.NotificationDTO;
import com.something.restaurantpos.entity.Notification;
import com.something.restaurantpos.entity.Role;

import java.util.List;

public interface INotificationService {
    void sendToUser(NotificationDTO notificationDTO, Role.UserRole role);
    void create(String message, Notification.NotificationType notificationType, Role.UserRole role);
}
