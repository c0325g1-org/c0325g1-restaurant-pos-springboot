package com.something.restaurantpos.repository;

import com.something.restaurantpos.entity.NotificationReceiver;
import org.springframework.data.jpa.repository.JpaRepository;

public interface INotificationReceiverRepository extends JpaRepository<NotificationReceiver, Integer> {
}
