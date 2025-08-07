package com.something.restaurantpos.controller.waiter;

import com.something.restaurantpos.service.impl.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class EmailController {
@Autowired
private EmailService emailService;
    @GetMapping("/sendMailsToAll")
    public String sendMailsToAll(@RequestParam String subject){
        emailService.sendVoucherToAllBookings(subject);
        return "redirect:/home";
    }
}
