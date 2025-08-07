package com.something.restaurantpos.controller;

import com.something.restaurantpos.dto.ChangePasswordDTO;
import com.something.restaurantpos.service.IAccountActivationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequiredArgsConstructor
public class AccountActivationController {

    private final IAccountActivationService activationService;

    @GetMapping("/activate-account")
    public String activateAccount(@RequestParam("token") String token, RedirectAttributes redirectAttributes, HttpSession session) {
        boolean success = activationService.activateAccount(token);
        
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "Tài khoản đã được kích hoạt thành công!");
            session.setAttribute("activationToken", token);
            return "redirect:/change-password-after-activation";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Link kích hoạt không hợp lệ hoặc đã hết hạn. Vui lòng liên hệ quản trị viên để được hỗ trợ.");
            return "redirect:/login";
        }
    }
    
    @GetMapping("/change-password-after-activation")
    public String showChangePasswordForm(Model model, HttpSession session) {
        if (!model.containsAttribute("changePasswordDTO")) {
            ChangePasswordDTO dto = new ChangePasswordDTO();
            // Lấy token từ session
            String token = (String) session.getAttribute("activationToken");
            if (token != null) {
                dto.setToken(token);
            } else {
                return "redirect:/login";
            }
            model.addAttribute("changePasswordDTO", dto);
        }
        return "auth/change_password_after_activation";
    }
    
    @PostMapping("/change-password-after-activation")
    public String changePasswordAfterActivation(@Valid ChangePasswordDTO changePasswordDTO, 
                                               BindingResult bindingResult, 
                                               RedirectAttributes redirectAttributes,
                                               HttpSession session) {
        
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.changePasswordDTO", bindingResult);
            redirectAttributes.addFlashAttribute("changePasswordDTO", changePasswordDTO);
            return "redirect:/change-password-after-activation";
        }
        
        // Kiểm tra mật khẩu xác nhận
        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu xác nhận không khớp!");
            redirectAttributes.addFlashAttribute("changePasswordDTO", changePasswordDTO);
            return "redirect:/change-password-after-activation";
        }
        
        // Lấy token từ session nếu không có trong DTO
        String token = changePasswordDTO.getToken();
        if (token == null || token.isEmpty()) {
            token = (String) session.getAttribute("activationToken");
        }
        
        // Thay đổi mật khẩu
        boolean success = activationService.changePasswordAfterActivation(token, changePasswordDTO.getNewPassword());
        
        if (success) {
            // Xóa token khỏi session sau khi thành công
            session.removeAttribute("activationToken");
            redirectAttributes.addFlashAttribute("successMessage", "Mật khẩu đã được thay đổi thành công! Bạn có thể đăng nhập ngay bây giờ.");
            return "redirect:/login";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Token không hợp lệ hoặc đã hết hạn!");
            return "redirect:/change-password-after-activation";
        }
    }
} 