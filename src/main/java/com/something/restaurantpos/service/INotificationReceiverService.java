package com.something.restaurantpos.service;

import com.something.restaurantpos.entity.Employee;
import com.something.restaurantpos.entity.NotificationReceiver;

import java.util.List;

public interface INotificationReceiverService {
    List<NotificationReceiver> findAllByEmployeeOrderByCreatedAtDesc(Employee employee);
    List<NotificationReceiver> findTop10ByEmployeeOrderByCreatedAtDesc(Employee employee);
    boolean markOneAsRead(Employee employee, Integer id);
    void deleteByEmployeeId(Integer id);
}
