package com.something.restaurantpos.controller.home;

import com.something.restaurantpos.entity.Feedback;
import com.something.restaurantpos.service.IFeedbackService;
import com.something.restaurantpos.service.IMenuItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/home")
public class HomeController {
    @Autowired
    private IMenuItemService menuItemService;
    @Autowired
    private IFeedbackService feedbackService;
    @GetMapping("")
    public String homePage(Model model){
        model.addAttribute("menuItems",menuItemService.findMenuItemOrderByTotalQuantityDesc());
        model.addAttribute("feedbacks",feedbackService.findTop5FiveStarFeedbacks(PageRequest.of(0,5)));
        return "pages/home/homePage";
    }
}
