package com.something.restaurantpos.controller;

import com.something.restaurantpos.entity.Feedback;
import com.something.restaurantpos.entity.Order;
import com.something.restaurantpos.repository.IOrderRepository;
import com.something.restaurantpos.service.IFeedbackService;
import com.something.restaurantpos.service.impl.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
@RequestMapping("/feedback")
public class FeedbackController {
    @Autowired
    private IOrderRepository orderRepository;
    @Autowired private IFeedbackService feedbackService;
    @Autowired
    private CloudinaryService cloudinaryService;

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
                         @RequestParam("customerPhone") String customerPhone,
                         @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {

        if (feedbackService.existsById(uuid)) return "pages/manager/already_rated";
        Order order = orderRepository.findByFeedbackToken(uuid);
        if (order == null) return "pages/manager/already_rated";

        Feedback f = new Feedback();
        f.setId(uuid);
        f.setRating(rating);
        f.setContent(content);
        f.setCustomerName(customerName);
        f.setCustomerPhone(customerPhone);

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String imageUrl = cloudinaryService.uploadImage(imageFile);
                f.setImagePath(imageUrl); 
            } catch (IOException e) {
                e.printStackTrace(); 
            }
        }

        feedbackService.save(f);
        return "redirect:/feedback/success";
    }

    @GetMapping("/success")
    public String successPage() {
        return "pages/manager/feedback_success";
    }
}
