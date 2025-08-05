package com.something.restaurantpos.controller.home;

import com.something.restaurantpos.dto.BookingDTO;
import com.something.restaurantpos.dto.MenuCategoryDTO;
import com.something.restaurantpos.entity.Booking;
import com.something.restaurantpos.entity.Feedback;
import com.something.restaurantpos.entity.MenuCategory;
import com.something.restaurantpos.service.IBookingService;
import com.something.restaurantpos.service.IFeedbackService;
import com.something.restaurantpos.service.IMenuItemService;
import com.something.restaurantpos.service.IVoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/home")
public class HomeController {
    private final IMenuItemService menuItemService;

    private final IFeedbackService feedbackService;
    private final IBookingService bookingService;

    private final IVoucherService voucherService;
    @GetMapping("")
    public String homePage(Model model) {
        model.addAttribute("bookingDTO", new BookingDTO());
        model.addAttribute("vouchers",voucherService.findAllByOrderByCreatedAtDesc(PageRequest.of(0,3)));
        model.addAttribute("menuItems", menuItemService.findMenuItemOrderByTotalQuantityDesc());
        model.addAttribute("feedbacks", feedbackService.findTop5FiveStarFeedbacks(PageRequest.of(0, 5)));
        return "pages/home/homePage";
    }

    @PostMapping("/booking")
    public String save(@Validated @ModelAttribute BookingDTO bookingDTO,
                       BindingResult bindingResult, RedirectAttributes attribute) throws IOException {
        if (bindingResult.hasErrors()) {
            return "pages/home/homePage";
        }

        Booking booking = new Booking();
        BeanUtils.copyProperties(bookingDTO, booking);
        bookingService.save(booking);
        return "pages/home/homeSuccess";
    }

}