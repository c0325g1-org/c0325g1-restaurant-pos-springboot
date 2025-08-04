package com.something.restaurantpos.repository;

import com.something.restaurantpos.entity.Employee;
import com.something.restaurantpos.entity.Notification;
import com.something.restaurantpos.entity.NotificationReceiver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface INotificationReceiverRepository extends JpaRepository<NotificationReceiver, Integer> {
    List<NotificationReceiver> findAllByEmployeeOrderByCreatedAtDesc(Employee employee);

    List<NotificationReceiver> findTop10ByEmployeeOrderByCreatedAtDesc(Employee employee);

    Optional<NotificationReceiver> findByIdAndEmployee(Integer id, Employee employee);
}
