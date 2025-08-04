    package com.something.restaurantpos.controller;

    import com.something.restaurantpos.dto.InvoiceDto;
    import com.something.restaurantpos.dto.PaymentDto;
    import com.something.restaurantpos.entity.Invoice;
    import com.something.restaurantpos.entity.Voucher;
    import com.something.restaurantpos.mapper.InvoiceMapper;
    import com.something.restaurantpos.repository.IMenuItemRepository;
    import com.something.restaurantpos.service.IInvoiceService;
    import com.something.restaurantpos.service.IPaymentService;
    import com.something.restaurantpos.service.IVoucherService;
    import com.something.restaurantpos.service.impl.CustomUserDetailsService;
    import com.something.restaurantpos.service.impl.QrCodeService;
    import jakarta.servlet.http.HttpServletRequest;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.beans.factory.annotation.Value;
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
    import java.util.List;


    @Controller
    @RequestMapping("/cashier")
    public class CashierController {

        @Value("${app.base-url}")
        private String baseUrl;
        @Autowired
        private IVoucherService voucherService;
        @Autowired
        private QrCodeService qrCodeService;

        @Autowired
        private CustomUserDetailsService userDetailsService;

        @Autowired
        private IInvoiceService invoiceService;
        @Autowired
        private IPaymentService paymentService;
        @Autowired
        private IMenuItemRepository menuItemRepository;

        @GetMapping("/dashboard")
        public String dashboard() {
            return "redirect:/cashier/tables";
        }
        @GetMapping("/invoices")
        public String listInvoices(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size, Model model) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
            Page<InvoiceDto> invoicePage = invoiceService.findAllDtoPage(pageable);
            model.addAttribute("invoices", invoicePage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", invoicePage.getTotalPages());
            model.addAttribute("totalItems", invoicePage.getTotalElements());
            return "pages/cashier/invoices";
        }

        @Autowired
        private InvoiceMapper invoiceMapper;

        @GetMapping("/{id}/invoice")
        public String showInvoiceDetail(@PathVariable Integer id, Model model) {
            List<Voucher> vouchers = voucherService.findAllValid(); // Hàm lọc theo thời gian và isActive
            InvoiceDto dto = invoiceService.findDtoById(id);
            model.addAttribute("invoice", dto);
            model.addAttribute("vouchers", vouchers);
            return "pages/cashier/checkout";
        }
        @PostMapping("/invoices/{id}/apply-voucher")
        public String applyVoucher(@PathVariable Integer id,
                                   @RequestParam Integer voucherId,
                                   RedirectAttributes redirectAttributes,Model model) {
            invoiceService.applyVoucher(id, voucherId);
            redirectAttributes.addFlashAttribute("success", "Áp dụng voucher thành công");
            return "redirect:/cashier/invoices/" + id + "/checkout";
        }

        @GetMapping("/invoices/print/{id}")
        public String printInvoice(@PathVariable Integer id, Model model) {
            InvoiceDto invoiceDto = invoiceService.findDtoById(id);
            if (invoiceDto == null) {
                throw new RuntimeException("Không tìm thấy hóa đơn");
            }
            List<Voucher> vouchers = voucherService.findAllValid();
            model.addAttribute("vouchers", vouchers);
            model.addAttribute("invoice", invoiceDto);
            model.addAttribute("newPayment", new PaymentDto());
            String uuid = invoiceService.findById(id).getOrder().getFeedbackToken();
            String verifyUrl = baseUrl + "/feedback/verify?uuid=" + uuid;
            String qrBase64 = qrCodeService.generateQRCodeBase64(verifyUrl, 200, 200);
            model.addAttribute("qrCode", qrBase64);

            return "pages/cashier/invoice_details";
        }


        @GetMapping("/invoices/{id}/checkout")
        public String checkoutPage(@PathVariable Integer id, Model model) {
            InvoiceDto invoiceDto = invoiceService.findDtoById(id);
            if (invoiceDto == null) {
                throw new RuntimeException("Không tìm thấy hóa đơn");
            }
            List<Voucher> vouchers = voucherService.findAllValid();
            PaymentDto payment = new PaymentDto();
            payment.setAmount(invoiceDto.getTotalAmount());
            model.addAttribute("invoice", invoiceDto);
            model.addAttribute("vouchers", vouchers);
            model.addAttribute("payment", payment);
            return "pages/cashier/checkout";
        }

        @PostMapping("/invoices/{id}/checkout")
        public String processCheckout(@PathVariable Integer id, @ModelAttribute PaymentDto paymentDto,
                                      RedirectAttributes redirectAttributes,
                                      HttpServletRequest request) throws UnsupportedEncodingException {
            paymentDto.setInvoiceId(id);
            invoiceService.processPayment(paymentDto);
            redirectAttributes.addFlashAttribute("success", "Thanh toán thành công!");
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
    }
