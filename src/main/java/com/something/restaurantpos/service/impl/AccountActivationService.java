package com.something.restaurantpos.service.impl;

import com.something.restaurantpos.entity.AccountActivationToken;
import com.something.restaurantpos.entity.Employee;
import com.something.restaurantpos.repository.IAccountActivationTokenRepository;
import com.something.restaurantpos.repository.IEmployeeRepository;
import com.something.restaurantpos.service.IAccountActivationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountActivationService implements IAccountActivationService {

    private final IAccountActivationTokenRepository activationTokenRepository;
    private final IEmployeeRepository employeeRepository;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Override
    @Transactional
    public void createActivationTokenAndSendEmail(Employee employee) {
        // Đảm bảo employee đã được lưu và có ID
        if (employee.getId() == null) {
            throw new IllegalArgumentException("Employee must be saved before creating activation token");
        }
        
        // Xóa token cũ nếu có
        activationTokenRepository.deleteByEmployeeId(employee.getId());
        
        // Tạo token mới
        AccountActivationToken token = new AccountActivationToken();
        token.setEmployee(employee);
        token.setUsed(false);
        
        // Lưu token để có ID và token được generate
        AccountActivationToken savedToken = activationTokenRepository.save(token);
        
        // Gửi email với token đã được lưu
        sendActivationEmail(employee, savedToken.getToken());
    }

    @Override
    @Transactional
    public boolean activateAccount(String token) {
        Optional<AccountActivationToken> tokenOpt = activationTokenRepository.findByTokenAndUsedFalse(token);
        
        if (tokenOpt.isEmpty()) {
            return false;
        }
        
        AccountActivationToken activationToken = tokenOpt.get();
        
        // Kiểm tra token có hết hạn chưa
        if (activationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return false;
        }
        
        // Kích hoạt tài khoản
        Employee employee = activationToken.getEmployee();
        employee.setEnable(true);
        employeeRepository.save(employee);
        
        // Đánh dấu token đã sử dụng
        activationToken.setUsed(true);
        activationTokenRepository.save(activationToken);
        
        return true;
    }

    @Override
    @Transactional
    public void resendActivationEmail(Integer employeeId) {
        Optional<Employee> employeeOpt = employeeRepository.findById(employeeId);
        if (employeeOpt.isPresent()) {
            Employee employee = employeeOpt.get();
            
            // Xóa token cũ
            activationTokenRepository.deleteByEmployeeId(employeeId);
            
            // Tạo token mới
            AccountActivationToken token = new AccountActivationToken();
            token.setEmployee(employee);
            token.setUsed(false);
            
            // Lưu token để có ID và token được generate
            AccountActivationToken savedToken = activationTokenRepository.save(token);
            
            // Gửi email với token đã được lưu
            sendActivationEmail(employee, savedToken.getToken());
        } else {
            throw new IllegalArgumentException("Employee not found with ID: " + employeeId);
        }
    }

    private void sendActivationEmail(Employee employee, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(employee.getEmail());
            helper.setSubject("Kích hoạt tài khoản - LEON");
            
            String activationLink = baseUrl + "/activate-account?token=" + token;
            
            // Tạo context cho template
            Context context = new Context();
            context.setVariable("employeeName", employee.getName());
            context.setVariable("activationLink", activationLink);
            context.setVariable("expiryMinutes", 24 * 60); // 24 giờ
            
            // Render template HTML
            String htmlContent = templateEngine.process("email/account_activation", context);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Không thể gửi email kích hoạt", e);
        }
    }
} 