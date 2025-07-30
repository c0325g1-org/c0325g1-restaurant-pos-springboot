package com.something.restaurantpos.service.impl;

import com.something.restaurantpos.dto.NotificationDTO;
import com.something.restaurantpos.entity.Employee;
import com.something.restaurantpos.entity.Notification;
import com.something.restaurantpos.entity.NotificationReceiver;
import com.something.restaurantpos.entity.Role;
import com.something.restaurantpos.repository.IEmployeeRepository;
import com.something.restaurantpos.repository.INotificationReceiverRepository;
import com.something.restaurantpos.repository.INotificationRepository;
import com.something.restaurantpos.service.INotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService implements INotificationService {

    private final INotificationRepository notificationRepository;
    private final INotificationReceiverRepository notificationReceiverRepository;
    private final IEmployeeRepository employeeRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void sendToUser(NotificationDTO notificationDTO, Role.UserRole role) {
        List<Employee> employeeList = employeeRepository.findAllByRole_UserRole(role);
        for (Employee employee : employeeList) {
            messagingTemplate.convertAndSend("/topic/user." + employee.getId(), notificationDTO);
        }
    }

    @Override
    public void create(String message, Notification.NotificationType notificationType, Role.UserRole role) {
        List<Employee> employeeList = employeeRepository.findAllByRole_UserRole(role);

        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setType(notificationType);
        notificationRepository.save(notification);

        for (Employee employee : employeeList) {
            NotificationReceiver receiver = new NotificationReceiver();
            receiver.setNotification(notification);
            receiver.setEmployee(employee);
            receiver.setIsRead(false);
            notificationReceiverRepository.save(receiver);
        }

    }
}
