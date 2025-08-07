package com.something.restaurantpos.controller.home;

import com.something.restaurantpos.controller.NotificationController;
import com.something.restaurantpos.dto.BookingDTO;
import com.something.restaurantpos.dto.MenuCategoryDTO;
import com.something.restaurantpos.dto.NotificationDTO;
import com.something.restaurantpos.entity.*;
import com.something.restaurantpos.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/home")
public class HomeController {
    private final IMenuItemService menuItemService;
    private final INotificationService notificationService;
    private final IFeedbackService feedbackService;
    private final IBookingService bookingService;

    private final IVoucherService voucherService;


    @GetMapping("")
    public String homePage(Model model) {
        model.addAttribute("bookingDTO", new BookingDTO());
        model.addAttribute("vouchers", voucherService.findByValidToAfter(LocalDateTime.now(), PageRequest.of(0, 5)));
        model.addAttribute("menuItems", menuItemService.findMenuItemOrderByTotalQuantityDesc());
        model.addAttribute("feedbacks", feedbackService.findTop5FiveStarFeedbacks(PageRequest.of(0, 5)));
        return "pages/home/homePage";
    }

    @PostMapping("/booking")
    @ResponseBody
    public ResponseEntity<?> saveBooking(@Validated @RequestBody BookingDTO bookingDTO, BindingResult result) {
        if (result.hasErrors()) {
            // Trả về danh sách lỗi dưới dạng Map<field, message>
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(errors);
        }
        NotificationDTO notificationDTO = new NotificationDTO();
        String message = bookingDTO.getName() + " đã đặt bàn mới!";
        notificationDTO.setMessage(message);
        notificationDTO.setSpeak(true);
        Booking booking = new Booking();
        BeanUtils.copyProperties(bookingDTO, booking);
        bookingService.save(booking);
        notificationService.create(message, Notification.NotificationType.INFO, Role.UserRole.ROLE_AGENT);
        notificationService.sendToUser(notificationDTO, Role.UserRole.ROLE_AGENT);

        return ResponseEntity.ok("success");
    }
    @GetMapping("/homeSuccess")
    public String saveSuccess(){
        return  "pages/home/homeSuccess";
    }

}