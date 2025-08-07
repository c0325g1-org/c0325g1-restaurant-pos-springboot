package com.something.restaurantpos.controller;

import com.something.restaurantpos.entity.Invoice;
import com.something.restaurantpos.repository.IInvoiceRepository;
import com.something.restaurantpos.service.IFeedbackService;
import com.something.restaurantpos.service.IInvoiceService;
import com.something.restaurantpos.service.impl.QrCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/invoices")
public class InvoiceController {

    @Value("${app.base-url}")
    private String baseUrl;
    @Autowired
    private IInvoiceService invoiceService;
    @Autowired
    private IFeedbackService feedbackService;
    @Autowired
    private QrCodeService qrCodeService;

    
    @GetMapping("/view/{orderId}")
    public String viewByOrder(@PathVariable Integer orderId, Model model, RedirectAttributes redirect) {
        Invoice invoice = invoiceService.findByOrderId(orderId);
        if (invoice == null) {
            redirect.addFlashAttribute("message", "Không tìm thấy hóa đơn!");
            return "redirect:/pages/manager/dashboard";
        }

        String uuid = invoice.getOrder().getFeedbackToken();
        boolean hasFeedback = feedbackService.existsById(uuid);

        String verifyUrl = baseUrl + "/feedback/verify?uuid=" + uuid;

        String qrBase64 = qrCodeService.generateQRCodeBase64(verifyUrl, 200, 200);

        model.addAttribute("invoice", invoice);
        model.addAttribute("feedbackUuid", uuid);
        model.addAttribute("hasFeedback", hasFeedback);
        model.addAttribute("qrCode", qrBase64);
        return "pages/manager/invoice_detail";
    }
    

    
}
