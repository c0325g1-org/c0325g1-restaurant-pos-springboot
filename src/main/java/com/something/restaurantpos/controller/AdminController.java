package com.something.restaurantpos.controller;

import com.something.restaurantpos.dto.EmployeeDTO;
import com.something.restaurantpos.dto.EmployeeUpdateDTO;
import com.something.restaurantpos.entity.Employee;
import com.something.restaurantpos.entity.Role;
import com.something.restaurantpos.mapper.EmployeeMapper;
import com.something.restaurantpos.repository.IEmployeeRepository;
import com.something.restaurantpos.repository.IInvoiceRepository;
import com.something.restaurantpos.repository.IRoleRepository;

import com.something.restaurantpos.repository.IAccountActivationTokenRepository;
import com.something.restaurantpos.entity.AccountActivationToken;
import com.something.restaurantpos.service.IAccountActivationService;

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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;


import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import com.something.restaurantpos.util.PasswordGenerator;


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
    private IAccountActivationTokenRepository activationTokenRepository;
    
    @Autowired
    private IAccountActivationService activationService;

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
            
            // Tạo mật khẩu ngẫu nhiên
            String randomPassword = PasswordGenerator.generatePassword();
            
            // Tạo employee mới
            Employee employee = employeeMapper.toEntity(employeeDTO);
            employee.setPassword(passwordEncoder.encode(randomPassword));
            employee.setRole(role);
            employee.setEnable(false); // Tài khoản chưa kích hoạt
            
            // Lưu employee và lấy employee đã được lưu (có ID)
            Employee savedEmployee = employeeRepository.save(employee);
            
            // Tạo token kích hoạt và gửi email
            try {
                activationService.createActivationTokenAndSendEmail(savedEmployee);
                redirectAttributes.addFlashAttribute("successMessage", 
                    "Thêm nhân viên thành công! Email kích hoạt đã được gửi đến " + savedEmployee.getEmail());
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("warningMessage", 
                    "Thêm nhân viên thành công nhưng không thể gửi email kích hoạt. Vui lòng kiểm tra cấu hình email.");
            }
            
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
            
            // Xóa token kích hoạt trước (nếu có)
            try {
                // Sử dụng service thay vì gọi repository trực tiếp
                activationService.deleteTokensByEmployeeId(id);
            } catch (Exception e) {
                // Log lỗi nhưng không dừng quá trình xóa
                System.err.println("Lỗi khi xóa token kích hoạt: " + e.getMessage());
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
    
    @PostMapping("/employees/validate-field")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> validateField(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        String fieldName = request.get("fieldName");
        String value = request.get("value");
        String formType = request.get("formType");
        String employeeId = request.get("employeeId");
        
        try {
            boolean isValid = true;
            String message = "";
            
            switch (fieldName) {
                case "name":
                    if (value == null || value.trim().isEmpty()) {
                        isValid = false;
                        message = "Họ tên không được để trống";
                    } else if (value.trim().length() < 2) {
                        isValid = false;
                        message = "Họ tên phải có ít nhất 2 ký tự";
                    } else if (!value.matches("^[a-zA-ZÀ-ỹ\\s]+$")) {
                        isValid = false;
                        message = "Họ tên chỉ được chứa chữ cái và khoảng trắng";
                    }
                    break;
                    
                case "username":
                    if (value == null || value.trim().isEmpty()) {
                        isValid = false;
                        message = "Tên đăng nhập không được để trống";
                    } else if (value.trim().length() < 3 || value.trim().length() > 50) {
                        isValid = false;
                        message = "Tên đăng nhập phải từ 3-50 ký tự";
                    } else if (!value.matches("^[a-zA-Z0-9_]+$")) {
                        isValid = false;
                        message = "Tên đăng nhập chỉ được chứa chữ cái, số và dấu gạch dưới";
                    } else {
                        // Kiểm tra trùng lặp username
                        Optional<Employee> existingEmployee = employeeRepository.findByUsername(value.trim());
                        if (existingEmployee.isPresent()) {
                            if ("edit".equals(formType) && employeeId != null) {
                                // Trong form edit, kiểm tra trừ nhân viên hiện tại
                                if (!existingEmployee.get().getId().toString().equals(employeeId)) {
                                    isValid = false;
                                    message = "Tên đăng nhập đã tồn tại";
                                }
                            } else {
                                // Trong form add hoặc không có employeeId
                                isValid = false;
                                message = "Tên đăng nhập đã tồn tại";
                            }
                        }
                    }
                    break;
                    
                case "email":
                    if (value == null || value.trim().isEmpty()) {
                        isValid = false;
                        message = "Email không được để trống";
                    } else if (!value.matches("^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$")) {
                        isValid = false;
                        message = "Email không đúng định dạng";
                    } else {
                        // Kiểm tra trùng lặp email
                        Optional<Employee> existingEmployee = employeeRepository.findByEmail(value.trim());
                        if (existingEmployee.isPresent()) {
                            if ("edit".equals(formType) && employeeId != null) {
                                // Trong form edit, kiểm tra trừ nhân viên hiện tại
                                if (!existingEmployee.get().getId().toString().equals(employeeId)) {
                                    isValid = false;
                                    message = "Email đã tồn tại";
                                }
                            } else {
                                // Trong form add hoặc không có employeeId
                                isValid = false;
                                message = "Email đã tồn tại";
                            }
                        }
                    }
                    break;
                    
                case "password":
                    // Trong form add, password không cần validate vì sẽ được tạo tự động
                    if ("add".equals(formType)) {
                        isValid = true;
                        message = "";
                    } else {
                        // Trong form edit, password optional
                        if (value != null && !value.trim().isEmpty()) {
                            if (value.trim().length() < 6) {
                                isValid = false;
                                message = "Mật khẩu phải có ít nhất 6 ký tự";
                            } else if (!value.matches("^[a-z0-9]+$")) {
                                isValid = false;
                                message = "Mật khẩu chỉ được chứa chữ thường và số";
                            }
                        }
                    }
                    break;
                    
                case "roleId":
                    if (value == null || value.trim().isEmpty()) {
                        isValid = false;
                        message = "Vui lòng chọn vai trò";
                    } else {
                        try {
                            Integer roleId = Integer.parseInt(value);
                            Role role = roleRepository.findById(roleId).orElse(null);
                            if (role == null) {
                                isValid = false;
                                message = "Vai trò không hợp lệ";
                            }
                        } catch (NumberFormatException e) {
                            isValid = false;
                            message = "Vai trò không hợp lệ";
                        }
                    }
                    break;
                    
                case "id":
                    // Field id không cần validate, luôn hợp lệ
                    isValid = true;
                    message = "";
                    break;
                    
                default:
                    isValid = false;
                    message = "Trường không hợp lệ: " + fieldName;
                    break;
            }
            
            response.put("valid", isValid);
            response.put("message", message);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("valid", false);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
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