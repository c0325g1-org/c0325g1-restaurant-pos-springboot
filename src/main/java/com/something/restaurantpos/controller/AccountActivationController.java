package com.something.restaurantpos.controller;

import com.something.restaurantpos.service.IAccountActivationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AccountActivationController {

    private final IAccountActivationService activationService;

    @GetMapping("/activate-account")
    public String activateAccount(@RequestParam("token") String token, RedirectAttributes redirectAttributes) {
        boolean success = activationService.activateAccount(token);
        
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "Tài khoản đã được kích hoạt thành công! Bạn có thể đăng nhập ngay bây giờ.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Link kích hoạt không hợp lệ hoặc đã hết hạn. Vui lòng liên hệ quản trị viên để được hỗ trợ.");
        }
        
        return "redirect:/activate-account-result";
    }
    
    @GetMapping("/activate-account-result")
    public String showActivationResult() {
        return "activate_account";
    }
} 