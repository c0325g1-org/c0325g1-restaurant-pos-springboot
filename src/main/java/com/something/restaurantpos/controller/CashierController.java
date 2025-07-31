package com.something.restaurantpos.controller;

import com.something.restaurantpos.config.VNPayConfig;
import com.something.restaurantpos.dto.InvoiceDto;
import com.something.restaurantpos.dto.PaymentDto;
import com.something.restaurantpos.entity.Invoice;
import com.something.restaurantpos.mapper.InvoiceMapper;
import com.something.restaurantpos.repository.IMenuItemRepository;
import com.something.restaurantpos.service.IInvoiceService;
import com.something.restaurantpos.service.IPaymentService;
import com.something.restaurantpos.service.impl.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.something.restaurantpos.config.VNPayConfig.vnp_Command;
import static com.something.restaurantpos.config.VNPayConfig.vnp_Version;

@Controller
@RequestMapping("/cashier")
public class CashierController {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private IInvoiceService invoiceService;
    @Autowired
    private IPaymentService paymentService;
    @Autowired
    private IMenuItemRepository menuItemRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        String username = authentication.getName();
        String employeeName = userDetailsService.getEmployeeNameByUsername(username);
        model.addAttribute("employeeName", employeeName);
        return "pages/cashier/dashboard";
    }
    @GetMapping("/invoices")
    public String listInvoices(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size, Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<InvoiceDto> invoicePage = invoiceService.findAllDtoPage(pageable);

        model.addAttribute("invoices", invoicePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", invoicePage.getTotalPages());
        model.addAttribute("totalItems", invoicePage.getTotalElements());

        return "pages/cashier/invoices"; // đường dẫn đến file HTML bạn đã dán ở trên
    }

    @Autowired
    private InvoiceMapper invoiceMapper;

    @GetMapping("/{id}/invoice")
    public String showInvoiceDetail(@PathVariable Integer id, Model model) {
        InvoiceDto dto = invoiceService.findDtoById(id); //
        model.addAttribute("invoice", dto);
        return "pages/cashier/invoice_details";
    }

    @GetMapping("/invoices/print/{id}")
    public String printInvoice(@PathVariable Integer id, Model model) {
        InvoiceDto invoiceDto = invoiceService.findDtoById(id);
        if (invoiceDto == null) {
            throw new RuntimeException("Không tìm thấy hóa đơn");
        }
        model.addAttribute("invoice", invoiceDto);
        return "pages/cashier/print_bill";
    }

    @GetMapping("/invoices/{id}/checkout")
    public String checkoutPage(@PathVariable Integer id, Model model) {
        InvoiceDto invoiceDto = invoiceService.findDtoById(id);
        if (invoiceDto == null) {
            throw new RuntimeException("Không tìm thấy hóa đơn");
        }

        PaymentDto payment = new PaymentDto();
        payment.setAmount(invoiceDto.getTotalAmount());

        model.addAttribute("invoice", invoiceDto);
        model.addAttribute("payment", payment);


        return "pages/cashier/checkout";
    }

    @PostMapping("/invoices/{id}/checkout")
    public String processCheckout(@PathVariable Integer id, @ModelAttribute PaymentDto paymentDto,
                                  RedirectAttributes redirectAttributes,
                                  HttpServletRequest request) throws UnsupportedEncodingException {
        paymentDto.setInvoiceId(id); // ✅ Gán trước
        if (paymentDto.getMethod().equals("VNPAY")) {
            return "redirect:" + createVNPayUrl(paymentDto, request);
        } else {
            invoiceService.processPayment(paymentDto);           // OK, giờ đã có id
            paymentService.save(paymentDto);
            redirectAttributes.addFlashAttribute("success", "Thanh toán thành công!");
            return "redirect:/cashier/invoices";// OK
        }
    }
    @GetMapping("/invoices/edit/{id}")
    public String editInvoiceForm(@PathVariable Integer id, Model model) {
        Invoice invoice = invoiceService.findById(id);
        InvoiceDto dto = invoiceService.findDtoById(id);
        if (invoice.getDiningTable() == null) {
            System.out.println("⚠️ DiningTable is null for invoice " + id);
        }
        model.addAttribute("invoice", invoice);
        model.addAttribute("invoiceDto", dto);
        model.addAttribute("menuItems", menuItemRepository.findAll());
        return "pages/cashier/edit_invoice"; // Tạo trang HTML tương ứng
    }
    @PostMapping("/invoices/update/{id}")
    public String updateInvoice(@PathVariable Integer id,
                                @ModelAttribute("invoice") InvoiceDto invoiceDto) {
        invoiceService.updateInvoiceWithItems(id, invoiceDto);
        return "redirect:/cashier/invoices";
    }
    @GetMapping("/checkout/{id}")
    public String showCheckout(@PathVariable Integer id, Model model) {
        Invoice invoice = invoiceService.findById(id);

        PaymentDto dto = new PaymentDto();
        dto.setAmount(invoice.getTotalAmount());

        model.addAttribute("invoice", invoice);
        model.addAttribute("payment", dto);
        return "cashier/checkout";
    }
    public String createVNPayUrl(@ModelAttribute PaymentDto paymentDto, HttpServletRequest request) throws UnsupportedEncodingException {
        long amount = (long) (paymentDto.getAmount().doubleValue() * 100 * 1000 );
        String vnp_TxnRef = paymentDto.getInvoiceId().toString();

        String vnp_TmnCode = VNPayConfig.vnp_TmnCode;
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_BankCode", "NCB");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", VNPayConfig.getIpAddress(request));
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
        String orderType = "billpayment";
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_Locale", "vn");


        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        return VNPayConfig.vnp_PayUrl + "?" + queryUrl;
    }

}
