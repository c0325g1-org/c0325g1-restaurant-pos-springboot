package com.something.restaurantpos.controller;

import com.something.restaurantpos.entity.Feedback;
import com.something.restaurantpos.entity.Order;
import com.something.restaurantpos.repository.IOrderRepository;
import com.something.restaurantpos.service.IFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/feedback")
public class FeedbackController {
    @Autowired
    private IOrderRepository orderRepository;
    @Autowired private IFeedbackService feedbackService;

    @GetMapping("/verify")
    public String verify(@RequestParam("uuid") String uuid, Model model) {
        Order order = orderRepository.findByFeedbackToken(uuid);
        if (order == null) {
            model.addAttribute("message", "Mã không hợp lệ.");
            return "pages/manager/already_rated"; 
        }

        if (feedbackService.existsById(uuid)) {
            model.addAttribute("message", "Bạn đã đánh giá đơn hàng này rồi.");
            return "pages/manager/already_rated";
        }

        model.addAttribute("uuid", uuid);
        return "pages/manager/feedback";
    }

    @PostMapping("/submit")
    public String submit(@RequestParam("uuid") String uuid,
                         @RequestParam("rating") Integer rating,
                         @RequestParam("content") String content,
                         @RequestParam("customerName") String customerName,
                         @RequestParam("customerPhone") String customerPhone) {
        if (feedbackService.existsById(uuid)) {
            return "pages/manager/already_rated";
        }

        if (orderRepository.findByFeedbackToken(uuid) == null) {
            return "pages/manager/already_rated";
        }

        Feedback f = new Feedback();
        f.setId(uuid);
        f.setRating(rating);
        f.setContent(content);
        f.setCustomerName(customerName);
        f.setCustomerPhone(customerPhone);
        feedbackService.save(f);

        return "pages/manager/feedback_success";
    }

}
