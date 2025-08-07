package com.something.restaurantpos.controller;

import com.something.restaurantpos.dto.EmployeeDTO;
import com.something.restaurantpos.dto.EmployeeUpdateDTO;
import com.something.restaurantpos.entity.Employee;
import com.something.restaurantpos.entity.Role;
import com.something.restaurantpos.mapper.EmployeeMapper;
import com.something.restaurantpos.repository.IEmployeeRepository;
import com.something.restaurantpos.repository.IInvoiceRepository;
import com.something.restaurantpos.repository.IRoleRepository;
import com.something.restaurantpos.service.IInvoiceService;
import com.something.restaurantpos.service.IMenuItemService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private IMenuItemService menuItemService;

    @Autowired
    private IEmployeeRepository employeeRepository;

    @Autowired
    private IRoleRepository roleRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private IInvoiceService invoiceService;

    @Autowired
    private IInvoiceRepository invoiceRepository;

    @GetMapping("")
    public String adminHome() {
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        long totalEmployees = employeeRepository.countByDeletedFalse(); 
        long enabledEmployees = employeeRepository.countByDeletedFalseAndEnableTrue();
        
        long totalManagers = employeeRepository.countByRoleId(1); // QUẢN_LÝ
        long totalCashiers = employeeRepository.countByRoleId(2); // THU_NGÂN
        long totalWaiters = employeeRepository.countByRoleId(3); // PHỤC_VỤ
        long totalKitchen = employeeRepository.countByRoleId(4); // BẾP
        long totalAdmins = employeeRepository.countByRoleId(5); // QUẢN_TRỊ

        model.addAttribute("pageTitle", "Admin Dashboard");
        model.addAttribute("employeeName", "Admin");

        model.addAttribute("totalEmployees", totalEmployees);
        model.addAttribute("enabledEmployees", enabledEmployees);
        
        model.addAttribute("totalManagers", totalManagers);
        model.addAttribute("totalCashiers", totalCashiers);
        model.addAttribute("totalWaiters", totalWaiters);
        model.addAttribute("totalKitchen", totalKitchen);
        model.addAttribute("totalAdmins", totalAdmins);
        long invoiceToday = invoiceService.countPaidInvoicesToday();
        model.addAttribute("invoiceToday", invoiceToday);
        BigDecimal revenueToday = invoiceService.sumRevenueToday();
        model.addAttribute("revenueToday", revenueToday);
        long sellingItems = menuItemService.countSellingItems();
        model.addAttribute("sellingItems", sellingItems);
        return "pages/admin/dashboard";
    }

    @GetMapping("/employees")
    public String employees(Model model) {
        List<Employee> employees = employeeRepository.findAllWithRole();
        List<Role> roles = roleRepository.findAll();
        
        // Sắp xếp theo ID từ nhỏ đến lớn
        employees.sort((e1, e2) -> e1.getId().compareTo(e2.getId()));
        
        model.addAttribute("pageTitle", "Quản lý Nhân viên");
        model.addAttribute("employeeName", "Admin");
        model.addAttribute("employees", employees);
        model.addAttribute("roles", roles);
        model.addAttribute("employeeDTO", new EmployeeDTO());
        model.addAttribute("employeeUpdateDTO", new EmployeeUpdateDTO());
        
        return "pages/admin/employees";
    }
    
    @PostMapping("/employees/toggle-status")
    public String toggleEmployeeStatus(@RequestParam Integer employeeId, RedirectAttributes redirectAttributes) {
        try {
            Employee employee = employeeRepository.findById(employeeId).orElse(null);
            if (employee == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy nhân viên");
                return "redirect:/admin/employees";
            }
            
            // Toggle trạng thái
            employee.setEnable(!employee.getEnable());
            employeeRepository.save(employee);
            
            String message = employee.getEnable() ? "Đã kích hoạt tài khoản" : "Đã vô hiệu hóa tài khoản";
            redirectAttributes.addFlashAttribute("successMessage", message);
            return "redirect:/admin/employees";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/admin/employees";
        }
    }
    
    @PostMapping("/employees/add")
    public String addEmployee(@ModelAttribute("employeeDTO") @Valid EmployeeDTO employeeDTO,
                             BindingResult result,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        try {
            // Kiểm tra validation errors
            if (result.hasErrors()) {
                List<Role> roles = roleRepository.findAll();
                model.addAttribute("roles", roles);
                model.addAttribute("pageTitle", "Quản lý Nhân viên");
                model.addAttribute("employeeName", "Admin");
                return "pages/admin/employees";
            }
            
            // Kiểm tra username đã tồn tại chưa
            if (employeeRepository.findByUsername(employeeDTO.getUsername()).isPresent()) {
                result.rejectValue("username", "error.username", "Username đã tồn tại");
                List<Role> roles = roleRepository.findAll();
                model.addAttribute("roles", roles);
                model.addAttribute("pageTitle", "Quản lý Nhân viên");
                model.addAttribute("employeeName", "Admin");
                return "pages/admin/employees";
            }
            
            // Kiểm tra email đã tồn tại chưa
            if (employeeRepository.findByEmail(employeeDTO.getEmail()).isPresent()) {
                result.rejectValue("email", "error.email", "Email đã tồn tại");
                List<Role> roles = roleRepository.findAll();
                model.addAttribute("roles", roles);
                model.addAttribute("pageTitle", "Quản lý Nhân viên");
                model.addAttribute("employeeName", "Admin");
                return "pages/admin/employees";
            }
            
            // Tìm role
            Role role = roleRepository.findById(employeeDTO.getRoleId()).orElse(null);
            if (role == null) {
                result.rejectValue("roleId", "error.roleId", "Vai trò không hợp lệ");
                List<Role> roles = roleRepository.findAll();
                model.addAttribute("roles", roles);
                model.addAttribute("pageTitle", "Quản lý Nhân viên");
                model.addAttribute("employeeName", "Admin");
                return "pages/admin/employees";
            }
            
            // Tạo employee mới
            Employee employee = employeeMapper.toEntity(employeeDTO);
            employee.setPassword(passwordEncoder.encode(employeeDTO.getPassword()));
            employee.setRole(role);
            employee.setEnable(true);
            
            employeeRepository.save(employee);
            
            redirectAttributes.addFlashAttribute("successMessage", "Thêm nhân viên thành công");
            return "redirect:/admin/employees";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/admin/employees";
        }
    }
    
    @GetMapping("/employees/{id}/edit")
    public String showEditForm(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Employee employee = employeeRepository.findById(id).orElse(null);
            if (employee == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy nhân viên");
                return "redirect:/admin/employees";
            }
            
            List<Role> roles = roleRepository.findAll();
            
            model.addAttribute("pageTitle", "Chỉnh sửa Nhân viên");
            model.addAttribute("employeeName", "Admin");
            model.addAttribute("employee", employee);
            model.addAttribute("roles", roles);
            
            return "pages/admin/employees";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/admin/employees";
        }
    }
    
    @PostMapping("/employees/{id}/update")
    public String updateEmployee(@PathVariable Integer id,
                                @RequestParam String name,
                                @RequestParam String username,
                                @RequestParam String email,
                                @RequestParam(required = false) String password,
                                @RequestParam Integer roleId,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        try {
            // Tạo DTO để validate
            EmployeeUpdateDTO employeeUpdateDTO = new EmployeeUpdateDTO();
            employeeUpdateDTO.setId(id);
            employeeUpdateDTO.setName(name);
            employeeUpdateDTO.setUsername(username);
            employeeUpdateDTO.setEmail(email);
            employeeUpdateDTO.setRoleId(roleId);
            
            // Chỉ validate password nếu có nhập
            if (password != null && !password.trim().isEmpty()) {
                employeeUpdateDTO.setPassword(password);
            }
            
            // Validate DTO (chỉ validate các field bắt buộc)
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<EmployeeUpdateDTO>> violations = validator.validate(employeeUpdateDTO);
            
            // Nếu có password được nhập, validate password riêng
            if (password != null && !password.trim().isEmpty()) {
                EmployeeUpdateDTO passwordDTO = new EmployeeUpdateDTO();
                passwordDTO.setPassword(password);
                Set<ConstraintViolation<EmployeeUpdateDTO>> passwordViolations = validator.validateProperty(passwordDTO, "password");
                violations.addAll(passwordViolations);
            }
            
            if (!violations.isEmpty()) {
                // Có lỗi validation
                List<Role> roles = roleRepository.findAll();
                model.addAttribute("roles", roles);
                model.addAttribute("pageTitle", "Quản lý Nhân viên");
                model.addAttribute("employeeName", "Admin");
                model.addAttribute("employees", employeeRepository.findAllWithRole());
                model.addAttribute("employeeDTO", new EmployeeDTO());
                model.addAttribute("employeeUpdateDTO", new EmployeeUpdateDTO());
                
                // Thêm thông báo lỗi
                StringBuilder errorMessage = new StringBuilder();
                for (ConstraintViolation<EmployeeUpdateDTO> violation : violations) {
                    errorMessage.append(violation.getMessage()).append("; ");
                }
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage.toString());
                return "redirect:/admin/employees";
            }
            
            Employee employee = employeeRepository.findById(id).orElse(null);
            if (employee == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy nhân viên");
                return "redirect:/admin/employees";
            }
            
            // Kiểm tra username đã tồn tại chưa (trừ nhân viên hiện tại)
            Optional<Employee> existingByUsername = employeeRepository.findByUsername(username);
            if (existingByUsername.isPresent() && !existingByUsername.get().getId().equals(id)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Username đã tồn tại");
                return "redirect:/admin/employees";
            }
            
            // Kiểm tra email đã tồn tại chưa (trừ nhân viên hiện tại)
            Optional<Employee> existingByEmail = employeeRepository.findByEmail(email);
            if (existingByEmail.isPresent() && !existingByEmail.get().getId().equals(id)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Email đã tồn tại");
                return "redirect:/admin/employees";
            }
            
            // Tìm role
            Role role = roleRepository.findById(roleId).orElse(null);
            if (role == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Vai trò không hợp lệ");
                return "redirect:/admin/employees";
            }
            
            // Cập nhật thông tin
            employee.setName(name);
            employee.setUsername(username);
            employee.setEmail(email);
            employee.setRole(role);
            
            // Chỉ cập nhật mật khẩu nếu có nhập
            if (password != null && !password.trim().isEmpty()) {
                employee.setPassword(passwordEncoder.encode(password));
            }
            
            employeeRepository.save(employee);
            
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật nhân viên thành công");
            return "redirect:/admin/employees";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/admin/employees";
        }
    }
    
    @PostMapping("/employees/{id}/delete")
    public String deleteEmployee(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            Employee employee = employeeRepository.findById(id).orElse(null);
            if (employee == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy nhân viên");
                return "redirect:/admin/employees";
            }
            
            // Kiểm tra xem nhân viên có phải là admin không
            if (employee.getRole().getName().equals("QUẢN_TRỊ")) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa tài khoản quản trị");
                return "redirect:/admin/employees";
            }
            
            // Kiểm tra xem nhân viên có đang hoạt động không
            if (employee.getEnable()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa nhân viên đang hoạt động. Vui lòng vô hiệu hóa trước");
                return "redirect:/admin/employees";
            }
            
            // Xóa nhân viên
            employeeRepository.delete(employee);
            
            redirectAttributes.addFlashAttribute("successMessage", "Xóa nhân viên thành công");
            return "redirect:/admin/employees";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/admin/employees";
        }
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
            response.put((String) row[0], ((Long) row[1]).intValue()); // tên món -> số lượng bán
        }

        return response;
    }
} 