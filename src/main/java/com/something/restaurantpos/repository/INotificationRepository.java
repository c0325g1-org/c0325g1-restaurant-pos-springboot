package com.something.restaurantpos.repository;

import com.something.restaurantpos.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface INotificationRepository extends JpaRepository<Notification, Integer> {
}
