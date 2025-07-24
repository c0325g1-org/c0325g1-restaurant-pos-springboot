package com.something.restaurantpos.controller.manager;

import com.something.restaurantpos.dto.VoucherDTO;
import com.something.restaurantpos.entity.Voucher;
import com.something.restaurantpos.mapper.VoucherMapper;
import com.something.restaurantpos.service.IVoucherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/manager/vouchers")
public class VoucherController {

    private final IVoucherService voucherService;
    private final VoucherMapper voucherMapper;
    private int pageSize = 10;

    @GetMapping
    public String list(@RequestParam(value = "keyword", required = false) String keyword,
                       @RequestParam(value = "status", required = false) Boolean status,
                       @RequestParam(value = "percent", required = false) Integer percent,
                       @RequestParam(value = "sort", required = false) String sort,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       Model model) {

        Sort sortOption = Sort.by("createdAt").descending();
        if ("oldest".equalsIgnoreCase(sort)) {
            sortOption = Sort.by("createdAt").ascending();
        }

        Pageable pageable = PageRequest.of(page, pageSize, sortOption);
        Page<Voucher> vouchers = voucherService.search(keyword, status, percent, pageable);
        long trashCount = voucherService.countDeleted();

        model.addAttribute("vouchers", vouchers);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("percent", percent);
        model.addAttribute("sort", sort);
        model.addAttribute("trashCount", trashCount);

        return "pages/manager/vouchers/list";
    }


    @GetMapping("/trash")
    public String trash(@RequestParam(value = "page", defaultValue = "0") int page,
                        Model model) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());
        Page<Voucher> trashVouchers = voucherService.findTrash(pageable);
        model.addAttribute("vouchers", trashVouchers);
        return "pages/manager/vouchers/trash";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("voucher", new VoucherDTO());
        return "pages/manager/vouchers/form";
    }

    @PostMapping("/create")
    public String save(@ModelAttribute("voucher") @Valid VoucherDTO voucherDTO,
                       BindingResult result,
                       RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) return "pages/manager/vouchers/form";
        Voucher voucher = voucherMapper.toEntity(voucherDTO);
        voucher.setId(null);
        voucherService.save(voucher);
        redirectAttributes.addFlashAttribute("successMessage", "Thêm mã giảm giá thành công!");
        return "redirect:/manager/vouchers";
    }

    @GetMapping("{id}/edit")
    public String edit(@PathVariable Integer id, Model model) {
        Voucher voucher = voucherService.findByIdOrThrow(id);
        model.addAttribute("voucher", voucherMapper.toDTO(voucher));
        return "pages/manager/vouchers/form";
    }

    @PostMapping("{id}/edit")
    public String update(@PathVariable Integer id,
                         @ModelAttribute("voucher") @Valid VoucherDTO voucherDTO,
                         BindingResult result,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) return "pages/manager/vouchers/form";
        voucherService.findByIdOrThrow(id);
        Voucher voucher = voucherMapper.toEntity(voucherDTO);
        voucher.setId(id);
        voucherService.save(voucher);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật mã giảm giá thành công!");
        return "redirect:/manager/vouchers";
    }

    @PostMapping("{id}/delete")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        voucherService.softDelete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Đã chuyển mã giảm giá vào thùng rác!");
        return "redirect:/manager/vouchers";
    }

    @PostMapping("{id}/restore")
    public String restore(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        voucherService.restore(id);
        redirectAttributes.addFlashAttribute("successMessage", "Khôi phục mã giảm giá thành công!");
        return "redirect:/manager/vouchers/trash";
    }

    @PostMapping("{id}/destroy")
    public String destroy(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        voucherService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Xóa vĩnh viễn mã giảm giá!");
        return "redirect:/manager/vouchers/trash";
    }
}

