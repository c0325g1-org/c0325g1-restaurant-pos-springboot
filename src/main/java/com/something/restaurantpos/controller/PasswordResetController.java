package com.something.restaurantpos.controller;

import com.something.restaurantpos.dto.ForgotPasswordDTO;
import com.something.restaurantpos.dto.ResetPasswordDTO;
import com.something.restaurantpos.service.IPasswordResetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PasswordResetController {

    @Autowired
    private IPasswordResetService passwordResetService;

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm(Model model) {
        model.addAttribute("forgotPasswordDTO", new ForgotPasswordDTO(null));
        return "/auth/forgot_password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@Valid ForgotPasswordDTO forgotPasswordDTO, 
                                       BindingResult bindingResult, 
                                       RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            return "/auth/forgot_password";
        }

        boolean success = passwordResetService.sendPasswordResetEmail(forgotPasswordDTO);
        
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", 
                "Email đặt lại mật khẩu đã được gửi. Vui lòng kiểm tra hộp thư của bạn.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Email không tồn tại trong hệ thống hoặc có lỗi xảy ra.");
        }
        
        return "redirect:/forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, 
                                       Model model, 
                                       RedirectAttributes redirectAttributes) {
        
        if (!passwordResetService.verifyToken(token)) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Token không hợp lệ hoặc đã hết hạn.");
            return "redirect:/login";
        }

        ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO(token, null, null);
        model.addAttribute("resetPasswordDTO", resetPasswordDTO);
        
        return "/auth/reset_password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@Valid ResetPasswordDTO resetPasswordDTO, 
                                      BindingResult bindingResult, 
                                      RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            return "/auth/reset_password";
        }

        // Kiểm tra mật khẩu có khớp không
        if (!resetPasswordDTO.isPasswordMatching()) {
            bindingResult.rejectValue("confirmPassword", "error.confirmPassword", 
                "Mật khẩu xác nhận không khớp.");
            return "/auth/reset_password";
        }

        boolean success = passwordResetService.resetPassword(resetPasswordDTO);
        
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", 
                "Mật khẩu đã được đặt lại thành công. Vui lòng đăng nhập với mật khẩu mới.");
            return "redirect:/login";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Có lỗi xảy ra khi đặt lại mật khẩu. Vui lòng thử lại.");
            return "redirect:/forgot-password";
        }
    }
} 