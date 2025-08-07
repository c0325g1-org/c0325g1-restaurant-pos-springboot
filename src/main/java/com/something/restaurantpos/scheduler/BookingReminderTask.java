package com.something.restaurantpos.scheduler;

import com.something.restaurantpos.dto.NotificationDTO;
import com.something.restaurantpos.entity.Booking;
import com.something.restaurantpos.entity.Notification;
import com.something.restaurantpos.entity.Role;
import com.something.restaurantpos.service.IBookingService;
import com.something.restaurantpos.service.INotificationService;
import com.something.restaurantpos.service.impl.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BookingReminderTask {
    private final IBookingService bookingService;
    private final INotificationService notificationService;


    @Scheduled(fixedRate = 60000)
    public void remindUpcomingBookings() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime next15 = now.plusMinutes(15);

        List<Booking> upcomingBookings = bookingService.findUpcomingUnremindedBookings(now, next15);

        for (Booking booking : upcomingBookings) {
            NotificationDTO notificationDTO = new NotificationDTO();
            String message = "Booking của khách " + booking.getName() + " (" + booking.getTable().getName() + ") sắp diễn ra lúc " + booking.getDateTime().format(DateTimeFormatter.ofPattern("HH:mm")) + "!";
            notificationDTO.setMessage(message);
            notificationDTO.setSpeak(true);
            booking.setReminded(true);
            bookingService.save(booking);
            notificationService.create(message, Notification.NotificationType.INFO, Role.UserRole.ROLE_AGENT);
            notificationService.sendToUser(notificationDTO, Role.UserRole.ROLE_AGENT);
        }
    }
}
