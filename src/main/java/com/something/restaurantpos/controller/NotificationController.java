package com.something.restaurantpos.controller;

import com.something.restaurantpos.dto.NotificationDTO;
import com.something.restaurantpos.dto.RequestBillDTO;
import com.something.restaurantpos.entity.*;
import com.something.restaurantpos.service.IEmployeeService;
import com.something.restaurantpos.service.INotificationReceiverService;
import com.something.restaurantpos.service.INotificationService;
import com.something.restaurantpos.service.impl.DiningTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final INotificationService notificationService;
    private final INotificationReceiverService notificationReceiverService;
    private final IEmployeeService employeeService;
    private final DiningTableService diningTableService;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping
    public ResponseEntity<List<NotificationDTO>> findAll(Authentication authentication) {
        String username = authentication.getName();
        Optional<Employee> employeeOpt = employeeService.findByUsername(username);
        if (employeeOpt.isPresent()) {
            List<NotificationReceiver> notificationReceivers = notificationReceiverService.findAllByEmployeeOrderByCreatedAtDesc(employeeOpt.get());
            return mapNotificationReceivers(notificationReceivers);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/lasted")
    public ResponseEntity<List<NotificationDTO>> lasted(Authentication authentication) {
        String username = authentication.getName();
        Optional<Employee> employeeOpt = employeeService.findByUsername(username);
        if (employeeOpt.isPresent()) {
            List<NotificationReceiver> notificationReceivers = notificationReceiverService.findTop10ByEmployeeOrderByCreatedAtDesc(employeeOpt.get());
            return mapNotificationReceivers(notificationReceivers);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    private ResponseEntity<List<NotificationDTO>> mapNotificationReceivers(List<NotificationReceiver> notificationReceivers) {
        List<NotificationDTO> dtos = notificationReceivers.stream().map(notificationReceiver ->
                new NotificationDTO(
                        notificationReceiver.getId(),
                        notificationReceiver.getNotification().getMessage(),
                        notificationReceiver.getNotification().getSenderEmployee() != null ? notificationReceiver.getNotification().getSenderEmployee().getName() : "Hệ thống",
                        notificationReceiver.getNotification().getSenderEmployee() != null ? notificationReceiver.getNotification().getSenderEmployee().getRole().getName() : "",
                        notificationReceiver.getNotification().getCreatedAt(),
                        notificationReceiver.getNotification().getType(),
                        notificationReceiver.getIsRead())).toList();
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markOneAsRead(@PathVariable Integer id, Authentication authentication) {
        String username = authentication.getName();
        Optional<Employee> employeeOpt = employeeService.findByUsername(username);
        if (employeeOpt.isPresent()) {
            boolean success = notificationReceiverService.markOneAsRead(employeeOpt.get(), id);
            return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/request-bill")
    public ResponseEntity<?> requestBill(@RequestBody RequestBillDTO dto) {
        DiningTable diningTable = diningTableService.findById(dto.getTableId());
        if (diningTable != null) {
            String message = diningTable.getName() + " yêu cầu thanh toán với " + dto.getBillType();
            NotificationDTO notificationDTO = new NotificationDTO();
            notificationDTO.setMessage(message);
            notificationService.create(message, Notification.NotificationType.INFO, Role.UserRole.ROLE_CASHIER);
            notificationService.sendToUser(notificationDTO, Role.UserRole.ROLE_CASHIER);
            return ResponseEntity.ok().body(Map.of(
                    "message", "Yêu cầu thanh toán đã được gửi thành công",
                    "status", "success"
            ));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "Đã xảy ra lỗi: Không tìm thấy bàn",
                    "status", "error"
            ));
        }
    }
}
