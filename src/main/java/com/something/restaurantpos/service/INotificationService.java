package com.something.restaurantpos.service;

import com.something.restaurantpos.entity.Notification;
import com.something.restaurantpos.entity.Role;

import java.util.List;

public interface INotificationService {
    <T> void sendToUser(T t, Role.UserRole role);
    void create(String message, Notification.NotificationType notificationType, Role.UserRole role);
}
