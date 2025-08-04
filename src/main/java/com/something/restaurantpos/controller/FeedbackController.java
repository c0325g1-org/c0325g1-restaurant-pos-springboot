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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        long trashCount = feedbackService.countByDeleted(true);

        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Page<Feedback> feedbackPage = feedbackService.search(keyword, PageRequest.of(page, size, direction, "createdAt"));
        List<Feedback> feedbackList = feedbackPage.getContent();

        List<String> tokens = feedbackList.stream().map(Feedback::getId).toList();
        List<Order> orders = orderRepository.findByFeedbackTokens(tokens);
        Map<String, Order> feedbackOrderMap = orders.stream()
                .collect(Collectors.toMap(Order::getFeedbackToken, o -> o));

        model.addAttribute("keyword", keyword);
        model.addAttribute("feedbackPage", feedbackPage);
        model.addAttribute("feedbacks", feedbackList);
        model.addAttribute("feedbackOrderMap", feedbackOrderMap);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("trashCount", trashCount);
        return "pages/manager/feedback_list";
    }


    @GetMapping("/verify")
    public String verify(@RequestParam("uuid") String uuid, Model model) {
        Order order = orderRepository.findByFeedbackToken(uuid);
        if (order == null) {
            model.addAttribute("message", "Mã không hợp lệ.");
            return "pages/manager/already_rated";
        }

        if (feedbackService.existsIncludingDeleted(uuid)) {
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
                         @RequestParam(value = "imageFiles", required = false) MultipartFile[] imageFiles,

                         Model model) {
        String uuid = feedbackDTO.getUuid();

        if (feedbackService.existsIncludingDeleted(uuid)) {
            model.addAttribute("message", "Bạn đã đánh giá đơn hàng này rồi.");
            return "pages/manager/already_rated";
        }

        if (result.hasErrors()) {
            model.addAttribute("uuid", uuid);
            return "pages/manager/feedback";
        }

        StringBuilder imagePaths = new StringBuilder();

        if (imageFiles != null) {
            for (MultipartFile file : imageFiles) {
                if (!file.isEmpty()) {
                    try {
                        String imageUrl = cloudinaryService.uploadImage(file);
                        imagePaths.append(imageUrl).append(";");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (imagePaths.length() > 0 && imagePaths.charAt(imagePaths.length() - 1) == ';') {
                imagePaths.deleteCharAt(imagePaths.length() - 1);
            }

            feedbackDTO.setImagePath(imagePaths.toString());
        }


        Order order = orderRepository.findByFeedbackToken(uuid);
        if (order == null) return "pages/manager/already_rated";

        Feedback f = new Feedback();
        f.setId(uuid);
        f.setCustomerName(feedbackDTO.getCustomerName());
        f.setCustomerPhone(feedbackDTO.getCustomerPhone());
        f.setContent(feedbackDTO.getContent());
        f.setRating(feedbackDTO.getRating());
        f.setImagePath(feedbackDTO.getImagePath() != null ? feedbackDTO.getImagePath() : "");

        feedbackService.save(f);
        return "redirect:/feedback/success";
    }

    @GetMapping("/success")
    public String successPage() {
        return "pages/manager/feedback_success";
    }

    @PreAuthorize("hasRole('QUẢN TRỊ')")
    @PostMapping("/delete")
    public String deleteFeedback(@RequestParam("id") String id) {
        Feedback feedback = feedbackService.findById(id);
        if (feedback != null) {
            feedback.markDeleted(); 
            feedbackService.save(feedback);
        }
        return "redirect:/feedback";
    }

    @PreAuthorize("hasRole('QUẢN TRỊ')")
    @PostMapping("/delete-multiple")
    public String deleteMultiple(@RequestParam(value = "selectedIds", required = false) List<String> ids,
                                 Model model) {
        if (ids != null && !ids.isEmpty()) {
            feedbackService.deleteMultipleByIds(ids);
        }
        return "redirect:/feedback";
    }

    @PreAuthorize("hasRole('QUẢN TRỊ')")
    @GetMapping("/trash")
    public String trash(@RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "") String keyword,
                        Model model) {
        Page<Feedback> feedbackPage = feedbackService.searchDeleted(keyword, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));

        model.addAttribute("feedbackPage", feedbackPage);
        model.addAttribute("feedbacks", feedbackPage.getContent());
        model.addAttribute("keyword", keyword);
        return "pages/manager/feedback_trash";
    }

    @PreAuthorize("hasRole('QUẢN TRỊ')")
    @PostMapping("/restore")
    public String restore(@RequestParam("id") String id) {
        feedbackService.restoreById(id);
        return "redirect:/feedback/trash";
    }

    @PreAuthorize("hasRole('QUẢN TRỊ')")
    @PostMapping("/destroy")
    public String destroy(@RequestParam("id") String id) {
        feedbackService.destroyById(id);
        return "redirect:/feedback/trash";
    }
}
