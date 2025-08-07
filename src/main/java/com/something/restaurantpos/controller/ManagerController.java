package com.something.restaurantpos.controller;

import com.something.restaurantpos.repository.IEmployeeRepository;
import com.something.restaurantpos.repository.IInvoiceRepository;
import com.something.restaurantpos.service.IFeedbackService;
import com.something.restaurantpos.service.IInvoiceService;
import com.something.restaurantpos.service.IMenuItemService;
import com.something.restaurantpos.service.IOrderService;
import com.something.restaurantpos.service.impl.QrCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/manager")
public class ManagerController {
    @Autowired
    private IMenuItemService menuItemService;

    @Autowired
    private IInvoiceRepository invoiceRepository;

    @Autowired
    private IEmployeeRepository employeeRepository;

    @Autowired
    private IFeedbackService feedbackService;
    
    @Autowired
    private IOrderService orderService;
    
    @Autowired
    private IInvoiceService invoiceService;
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        long totalEmployees = employeeRepository.countByDeletedFalse();
        long enabledEmployees = employeeRepository.countByDeletedFalseAndEnableTrue();

        model.addAttribute("totalEmployees", totalEmployees);
        model.addAttribute("enabledEmployees", enabledEmployees);
        
        long invoiceToday = invoiceService.countPaidInvoicesToday();
        model.addAttribute("invoiceToday", invoiceToday);
        BigDecimal revenueToday = invoiceService.sumRevenueToday();
        model.addAttribute("revenueToday", revenueToday);
        long sellingItems = menuItemService.countSellingItems();
        model.addAttribute("sellingItems", sellingItems);
        
        
        model.addAttribute("feedbacks", feedbackService.findAll());
        model.addAttribute("invoices", invoiceService.findAll());
        return "pages/manager/dashboard";
    }

    @GetMapping("/revenue")
    @ResponseBody
    public Map<String, BigDecimal> getRevenueByDateRange(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        List<Object[]> results = invoiceRepository.sumRevenueByDateRange(start, end);
        return results.stream().collect(Collectors.toMap(
                row -> ((java.sql.Date) row[0]).toLocalDate().toString(),
                row -> (BigDecimal) row[1]
        ));
    }

    @GetMapping("/top-items")
    @ResponseBody
    public Map<String, Integer> getTopSellingItemsThisMonth() {
        LocalDateTime start = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime end = LocalDate.now().plusMonths(1).withDayOfMonth(1).atStartOfDay().minusNanos(1);

        List<Object[]> result = menuItemService.getTopSellingItemsThisMonth(start, end);

        Map<String, Integer> response = new LinkedHashMap<>();
        for (Object[] row : result) {
            response.put((String) row[0], ((Long) row[1]).intValue()); 
        }

        return response;
    }
    
}
