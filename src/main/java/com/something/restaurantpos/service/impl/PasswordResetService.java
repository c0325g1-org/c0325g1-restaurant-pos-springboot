package com.something.restaurantpos.service.impl;

import com.something.restaurantpos.dto.ForgotPasswordDTO;
import com.something.restaurantpos.dto.ResetPasswordDTO;
import com.something.restaurantpos.entity.Employee;
import com.something.restaurantpos.entity.PasswordResetToken;
import com.something.restaurantpos.repository.IEmployeeRepository;
import com.something.restaurantpos.repository.IPasswordResetTokenRepository;
import com.something.restaurantpos.service.IPasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService implements IPasswordResetService {

    @Autowired
    private IEmployeeRepository employeeRepository;

    @Autowired
    private IPasswordResetTokenRepository tokenRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TemplateEngine templateEngine;

    // Thời gian hết hạn token (15 phút)
    private static final int TOKEN_EXPIRY_MINUTES = 15;

    @Override
    @Transactional
    public boolean sendPasswordResetEmail(ForgotPasswordDTO forgotPasswordDTO) {
        // Kiểm tra email có tồn tại trong hệ thống không
        Optional<Employee> employeeOpt = employeeRepository.findByEmail(forgotPasswordDTO.getEmail());
        if (employeeOpt.isEmpty()) {
            return false; // Email không tồn tại
        }

        // Xóa token cũ nếu có
        tokenRepository.deleteByEmail(forgotPasswordDTO.getEmail());

        // Tạo token mới
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(TOKEN_EXPIRY_MINUTES);
        
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setEmail(forgotPasswordDTO.getEmail());
        resetToken.setExpiryDate(expiryDate);
        resetToken.setUsed(false);
        tokenRepository.save(resetToken);

        // Gửi email
        sendResetEmail(forgotPasswordDTO.getEmail(), token);
        
        return true;
    }

    @Override
    public boolean verifyToken(String token) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);
        if (tokenOpt.isEmpty()) {
            return false;
        }

        PasswordResetToken resetToken = tokenOpt.get();
        
        // Kiểm tra token đã được sử dụng chưa
        if (resetToken.isUsed()) {
            return false;
        }

        // Kiểm tra token có hết hạn không
        if (LocalDateTime.now().isAfter(resetToken.getExpiryDate())) {
            return false;
        }

        return true;
    }

    @Override
    @Transactional
    public boolean resetPassword(ResetPasswordDTO resetPasswordDTO) {
        // Xác minh token
        if (!verifyToken(resetPasswordDTO.getToken())) {
            return false;
        }

        // Kiểm tra mật khẩu có khớp không
        if (!resetPasswordDTO.isPasswordMatching()) {
            return false;
        }

        // Lấy token
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(resetPasswordDTO.getToken());
        if (tokenOpt.isEmpty()) {
            return false;
        }

        PasswordResetToken resetToken = tokenOpt.get();

        // Tìm employee theo email
        Optional<Employee> employeeOpt = employeeRepository.findByEmail(resetToken.getEmail());
        if (employeeOpt.isEmpty()) {
            return false;
        }

        Employee employee = employeeOpt.get();

        // Cập nhật mật khẩu mới (mã hóa BCrypt)
        employee.setPassword(passwordEncoder.encode(resetPasswordDTO.getNewPassword()));
        employeeRepository.save(employee);

        // Đánh dấu token đã được sử dụng
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        return true;
    }

    // Gửi email chứa link reset password sử dụng HTML template
    private void sendResetEmail(String email, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(email);
            helper.setSubject("Đặt lại mật khẩu - Restaurant POS");
            
            // Tìm thông tin nhân viên
            Optional<Employee> employeeOpt = employeeRepository.findByEmail(email);
            String employeeName = "";
            if (employeeOpt.isPresent()) {
                employeeName = employeeOpt.get().getName();
            }
            
            // Tạo context cho Thymeleaf template
            Context context = new Context();
            context.setVariable("resetLink", "http://localhost:8080/reset-password?token=" + token);
            context.setVariable("expiryMinutes", TOKEN_EXPIRY_MINUTES);
            context.setVariable("employeeName", employeeName);
            
            // Render HTML template
            String htmlContent = templateEngine.process("email/password_reset", context);
            helper.setText(htmlContent, true); // true = HTML content
            
            mailSender.send(message);
            
        } catch (MessagingException e) {
            throw new RuntimeException("Không thể gửi email reset password", e);
        }
    }
} 