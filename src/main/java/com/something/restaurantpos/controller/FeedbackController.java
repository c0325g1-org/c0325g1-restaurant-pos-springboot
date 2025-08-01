package com.something.restaurantpos.controller;

import com.something.restaurantpos.dto.FeedbackDTO;
import com.something.restaurantpos.entity.Feedback;
import com.something.restaurantpos.entity.Order;
import com.something.restaurantpos.repository.IOrderRepository;
import com.something.restaurantpos.service.IFeedbackService;
import com.something.restaurantpos.service.impl.CloudinaryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/feedback")
public class FeedbackController {

    @Autowired
    private IOrderRepository orderRepository;

    @Autowired
    private IFeedbackService feedbackService;

    @Autowired
    private CloudinaryService cloudinaryService;
    
    private int size = 10;

    @GetMapping("")
    public String list(@RequestParam(name = "page", defaultValue = "0") int page,
                       @RequestParam(defaultValue = "") String keyword,
                       @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir,
                       Model model) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Page<Feedback> feedbacks = feedbackService.search(keyword, PageRequest.of(page, size, direction, "createdAt"));

        model.addAttribute("keyword", keyword);
        model.addAttribute("feedbacks", feedbacks.getContent());
        model.addAttribute("totalPages", feedbacks.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("sortDir", sortDir);
        return "pages/manager/feedback_list";
    }

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

        FeedbackDTO dto = new FeedbackDTO();
        dto.setUuid(uuid);
        model.addAttribute("feedbackDTO", dto);
        model.addAttribute("uuid", uuid);
        return "pages/manager/feedback";
    }

    @PostMapping("/submit")
    public String submit(@Valid FeedbackDTO feedbackDTO,
                         BindingResult result,
                         @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                         Model model) {
        String uuid = feedbackDTO.getUuid();

        if (feedbackService.existsById(uuid)) {
            model.addAttribute("message", "Bạn đã đánh giá đơn hàng này rồi.");
            return "pages/manager/already_rated";
        }

        if (result.hasErrors()) {
            model.addAttribute("uuid", uuid);
            return "pages/manager/feedback";
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String imageUrl = cloudinaryService.uploadImage(imageFile);
                feedbackDTO.setImagePath(imageUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Order order = orderRepository.findByFeedbackToken(uuid);
        if (order == null) return "pages/manager/already_rated";

        Feedback f = new Feedback();
        f.setId(uuid);
        f.setCustomerName(feedbackDTO.getCustomerName());
        f.setCustomerPhone(feedbackDTO.getCustomerPhone());
        f.setContent(feedbackDTO.getContent());
        f.setRating(feedbackDTO.getRating());
        f.setImagePath(feedbackDTO.getImagePath());

        feedbackService.save(f);
        return "redirect:/feedback/success";
    }

    @GetMapping("/success")
    public String successPage() {
        return "pages/manager/feedback_success";
    }
}
