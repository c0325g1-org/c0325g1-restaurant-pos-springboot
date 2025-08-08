    package com.something.restaurantpos.service.impl;

    import com.something.restaurantpos.entity.Booking;
    import com.something.restaurantpos.entity.Voucher;
    import com.something.restaurantpos.repository.IBookingRepository;
    import com.something.restaurantpos.repository.IVoucherRepository;
    import jakarta.mail.MessagingException;
    import jakarta.mail.internet.MimeMessage;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.mail.javamail.JavaMailSender;
    import org.springframework.mail.javamail.MimeMessageHelper;
    import org.springframework.scheduling.annotation.Scheduled;
    import org.springframework.stereotype.Service;
    import org.thymeleaf.TemplateEngine;
    import org.thymeleaf.context.Context;

    import java.time.LocalDateTime;
    import java.util.HashSet;
    import java.util.List;
    import java.util.Set;

    @Service
    public class EmailService {
        @Autowired
        private IBookingRepository bookingRepository;
        @Autowired
        private JavaMailSender mailSender;
        @Autowired
        private TemplateEngine templateEngine;
        @Autowired
        private IVoucherRepository voucherRepository;
        public void sendEmailWithVoucher(String to, String subject,String name) {
            Voucher voucher = voucherRepository.findTopByIsActiveTrueAndValidToAfterOrderByCreatedAtDesc(LocalDateTime.now());
            if (voucher == null) {
                System.out.println("Không có voucher hợp lệ để gửi.");
                return;
            }
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("voucher", voucher);
            String htmlContent = templateEngine.process("email/content-email", context);
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            try {
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(htmlContent, true);
                mailSender.send(mimeMessage);
                System.out.println("Đã gửi voucher đến: " + to);
            } catch (MessagingException e) {
                System.err.println("Lỗi khi gửi email đến: " + to);
                e.printStackTrace();
            }
        }
        public void sendVoucherToAllBookings(String subject) {
            List<Booking> bookings = bookingRepository.findAllByEmailIsNotNull();
            Set<String> sentEmails = new HashSet<>();// check trùng lặp email
            for (Booking booking : bookings) {
                String email = booking.getEmail();
                // Skip mail của dữ liệu mẫu
                if (email != null && email.contains("@example.com")) {
                    continue;
                }
                if (email != null && !email.trim().isEmpty() && sentEmails.add(email)) {
                    sendEmailWithVoucher(email, subject,booking.getName());
                }
            }
        }
        @Scheduled(cron = "0 00 10 * * ?")
        public void autoSendVoucherEmail() {
            System.out.println("Bắt đầu gửi tự động voucher đến khách hàng...");
            sendVoucherToAllBookings("🎁 Ưu đãi đặc biệt từ Nhà hàng LEON");
        }
    }