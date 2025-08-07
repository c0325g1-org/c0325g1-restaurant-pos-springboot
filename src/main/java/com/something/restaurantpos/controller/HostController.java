package com.something.restaurantpos.controller;

import com.something.restaurantpos.dto.BookingUpdateRequest;
import com.something.restaurantpos.entity.Booking;
import com.something.restaurantpos.entity.DiningTable;
import com.something.restaurantpos.service.IBookingService;
import com.something.restaurantpos.service.IDiningTableService;
import com.something.restaurantpos.service.impl.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/host")
public class HostController {
    private final IBookingService bookingService;
    private final IDiningTableService diningTableService;
    private int pageSize = 10;

    @GetMapping("/booking-list")
    public String list(@RequestParam(value = "keyword", required = false) String keyword,
                       @RequestParam(value = "status", required = false) Booking.BookingStatus status,
                       @RequestParam(value = "sort", required = false) String sort,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       Model model) {

        Sort sortOption = Sort.by("createdAt").descending();
        if ("oldest".equalsIgnoreCase(sort)) {
            sortOption = Sort.by("createdAt").ascending();
        }
        Pageable pageable = PageRequest.of(page, pageSize, sortOption);
        Page<Booking> bookings = bookingService.search(keyword, status, pageable);
        long trashCount = bookingService.countDeleted();
        model.addAttribute("bookings", bookings);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("sort", sort);
        model.addAttribute("trashCount", trashCount);

        return "pages/host/list";
    }


    @GetMapping("/trash")
    public String trash(@RequestParam(value = "page", defaultValue = "0") int page,
                        Model model) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());
        Page<Booking> trashBookings = bookingService.findTrash(pageable);
        model.addAttribute("bookings", trashBookings);
        return "pages/host/trash";
    }

    @PostMapping("{id}/delete")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        bookingService.softDelete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Đã chuyển booking vào thùng rác!");
        return "redirect:/host/booking-list";
    }

    @PostMapping("{id}/restore")
    public String restore(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        bookingService.restore(id);
        redirectAttributes.addFlashAttribute("successMessage", "Khôi phục booking thành công!");
        return "redirect:/host/trash";
    }

    @PostMapping("{id}/destroy")
    public String destroy(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        bookingService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Xóa vĩnh viễn booking!");
        return "redirect:/host/trash";
    }

    @GetMapping("/api/available-tables")
    @ResponseBody
    public Map<String, Object> getAvailableTables(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<DiningTable> tables = diningTableService.findAll();

        List<Booking> bookings = bookingService.findByDate(date).stream()
                .sorted(Comparator.comparing(Booking::getDateTime))
                .toList();

        Map<Integer, List<Map<String, Object>>> bookingMap = bookings.stream()
                .filter(b -> b.getTable() != null && b.getStatus() != Booking.BookingStatus.CANCELLED)
                .collect(Collectors.groupingBy(
                        b -> b.getTable().getId(),
                        Collectors.mapping(b -> {
                            Map<String, Object> map = new HashMap<>();
                            map.put("time", b.getDateTime().toLocalTime().toString());
                            map.put("tableName", b.getTable().getName());
                            map.put("name", b.getName());
                            map.put("people", b.getPeople());
                            map.put("status", b.getStatus().getLabel());
                            return map;
                        }, Collectors.toList())
                ));

        List<Map<String, Object>> tableList = tables.stream()
                .map(t -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", t.getId());
                    map.put("name", t.getName());
                    return map;
                })
                .toList();

        return Map.of(
                "tables", tableList,
                "bookings", bookingMap
        );
    }

    @PostMapping("/updateStatus")
    public String updateBookingStatus(@RequestParam("id") Integer id,
                                      @RequestParam("status") Booking.BookingStatus newStatus,
                                      RedirectAttributes redirectAttributes) {
        Booking booking = bookingService.findById(id);

        if (booking == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy booking #" + id);
            return "redirect:/host/booking-list";
        }

        Booking.BookingStatus currentStatus = booking.getStatus();

        if (currentStatus == Booking.BookingStatus.CONFIRMED
                && newStatus != Booking.BookingStatus.CONFIRMED
                && newStatus != Booking.BookingStatus.SERVED
        ) {
            booking.setTable(null);
        }
        booking.setStatus(newStatus);
        bookingService.save(booking);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái thành công.");
        return "redirect:/host/booking-list";
    }

    @PostMapping("/assignTable")
    public String assignTableToBooking(@RequestParam("id") Integer bookingId,
                                       @RequestParam("tableId") Integer tableId,
                                       RedirectAttributes redirectAttributes) {
        Booking booking = bookingService.findById(bookingId);
        DiningTable table = diningTableService.findById(tableId);

        if (booking == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy booking #" + bookingId);
            return "redirect:/host/booking-list";
        }

        if (table == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy bàn #" + tableId);
            return "redirect:/host/booking-list";
        }

        LocalDateTime targetTime = booking.getDateTime();
        LocalDateTime start = targetTime.minusHours(1);
        LocalDateTime end = targetTime.plusHours(1);

        boolean isConflict = bookingService.isTableBookedBetween(table, start, end, booking.getId());

        if (isConflict) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    table.getName() + " đã được đặt trong khung giờ từ "
                            + start.format(DateTimeFormatter.ofPattern("HH:mm"))
                            + " đến " + end.format(DateTimeFormatter.ofPattern("HH:mm")) + ".");
            return "redirect:/host/booking-list";
        }

        booking.setTable(table);
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        bookingService.save(booking);

        redirectAttributes.addFlashAttribute("successMessage", "Đã gán bàn \"" + table.getName() + "\" cho khách \"" + booking.getName() + "\".");
        return "redirect:/host/booking-list";
    }

    @PostMapping("/editBooking")
    public String updateBookingInfo(@ModelAttribute BookingUpdateRequest request, RedirectAttributes redirectAttributes) {
        bookingService.updateBookingInfo(request); // xử lý trong service
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin đặt bàn thành công!");
        return "redirect:/host/booking-list";
    }

    @GetMapping("/dashboard")
    public String tableOverview(@RequestParam(value = "date", required = false)
                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                Model model) {
        if (date == null) {
            date = LocalDate.now();
        }

        List<DiningTable> tables = diningTableService.findAll();
        List<Booking> bookings = bookingService.findByDate(date).stream()
                .sorted(Comparator.comparing(Booking::getDateTime))
                .collect(Collectors.toList());


        model.addAttribute("selectedDate", date);
        model.addAttribute("tables", tables);
        model.addAttribute("bookings", bookings);
        return "pages/host/tables-overview";
    }
}
