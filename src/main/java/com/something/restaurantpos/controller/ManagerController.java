package com.something.restaurantpos.controller;

import com.something.restaurantpos.service.IFeedbackService;
import com.something.restaurantpos.service.IInvoiceService;
import com.something.restaurantpos.service.IOrderService;
import com.something.restaurantpos.service.impl.QrCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/manager")
public class ManagerController {
    @Autowired
    private IFeedbackService feedbackService;
    
    @Autowired
    private IOrderService orderService;
    
    @Autowired
    private IInvoiceService invoiceService;
    
    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("feedbacks", feedbackService.findAll());
        model.addAttribute("invoices", invoiceService.findAll());
        model.addAttribute("orders", orderService.findAll());
        return "pages/manager/dashboard";
    }
    
}
