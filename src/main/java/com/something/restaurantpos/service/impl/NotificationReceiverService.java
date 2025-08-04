package com.something.restaurantpos.service.impl;

import com.something.restaurantpos.entity.Employee;
import com.something.restaurantpos.entity.NotificationReceiver;
import com.something.restaurantpos.repository.INotificationReceiverRepository;
import com.something.restaurantpos.service.INotificationReceiverService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationReceiverService implements INotificationReceiverService {

    private final INotificationReceiverRepository notificationReceiverRepository;
    @Override
    public List<NotificationReceiver> findAllByEmployeeOrderByCreatedAtDesc(Employee employee) {
        return notificationReceiverRepository.findAllByEmployeeOrderByCreatedAtDesc(employee);
    }

    @Override
    public List<NotificationReceiver> findTop10ByEmployeeOrderByCreatedAtDesc(Employee employee) {
        return notificationReceiverRepository.findTop10ByEmployeeOrderByCreatedAtDesc(employee);
    }

    @Override
    public boolean markOneAsRead(Employee employee, Integer id) {
        Optional<NotificationReceiver> optional = notificationReceiverRepository.findByIdAndEmployee(id, employee);
        if (optional.isPresent() && !optional.get().getIsRead()) {
            NotificationReceiver nr = optional.get();
            nr.setIsRead(true);
            notificationReceiverRepository.save(nr);
            return true;
        }
        return false;
    }
}
