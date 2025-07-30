package com.something.restaurantpos.controller.manager;

import com.something.restaurantpos.dto.MenuCategoryDTO;
import com.something.restaurantpos.dto.MenuItemDTO;
import com.something.restaurantpos.entity.MenuCategory;
import com.something.restaurantpos.entity.MenuItem;
import com.something.restaurantpos.service.IMenuCategoryService;
import com.something.restaurantpos.service.IMenuItemService;
import com.something.restaurantpos.service.impl.CloudinaryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Controller
@RequestMapping("/manager")
public class CategoryController {

    @Autowired
    private IMenuCategoryService menuCategoryService;

    private int size = 10;


    @GetMapping("/menu_category")
    public String getAll(Model model,
                         @RequestParam(name = "page", defaultValue = "0") int page) {
        Page<MenuCategory> menuCategories = menuCategoryService.findAllCategory(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));

        long trashCount = menuCategoryService.countDeleted();
        model.addAttribute("menuCategories", menuCategories);
        model.addAttribute("trashCount", trashCount);
        return "pages/manager/menu_category/list";
    }

    @GetMapping("/menu_category/trash")
    public String trash(@RequestParam(value = "page", defaultValue = "0") int page,
                        Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MenuCategory> trashMenuItems = menuCategoryService.findTrash(pageable);
        model.addAttribute( "menuCategories", trashMenuItems);
        return "pages/manager/menu_category/trash";
    }

    @PostMapping("/menu_category/{id}/restore")
    public String restore(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        menuCategoryService.restore(id);
        redirectAttributes.addFlashAttribute("successMessage", "Khôi phục loại món ăn thành công!");
        return "redirect:/manager/menu_category/trash";
    }

    @PostMapping("/menu_category/{id}/destroy")
    public String destroy(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        menuCategoryService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Xóa vĩnh viễn loại món ăn thành công!");
        return "redirect:/manager/menu_category/trash";
    }

    @GetMapping("/menu_category/save")
    public String create(Model model) {
        model.addAttribute("menuCategoryDTO", new MenuCategoryDTO());
        return "pages/manager/menu_category/create";
    }

    @PostMapping("/menu_category/save")
    public String save(@Validated @ModelAttribute MenuCategoryDTO menuCategoryDTO,
                       BindingResult bindingResult, RedirectAttributes attribute) throws IOException {
        if (bindingResult.hasErrors()) {
            return "pages/manager/menu_category/create";
        }

        MenuCategory menuCategory = new MenuCategory();
        BeanUtils.copyProperties(menuCategoryDTO, menuCategory);
        menuCategoryService.save(menuCategory);
        attribute.addFlashAttribute("successMessage", "Tạo món ăn thành công");
        attribute.addFlashAttribute("menuCategory", menuCategory);

        return "redirect:/manager/menu_category";
    }

    @GetMapping("/menu_category/{id}/edit")
    public String update(@PathVariable Integer id, @ModelAttribute MenuCategoryDTO menuCategoryDTO, RedirectAttributes attributes, Model model) throws IOException {
        MenuCategory menuCategory = menuCategoryService.findById(id);
        if (menuCategory == null) {
            attributes.addFlashAttribute("successMessage", "không tìm thấy id cần thay đổi");
            return "redirect:/manager/menu_category";
        }
        BeanUtils.copyProperties(menuCategory, menuCategoryDTO);
        model.addAttribute("menuCategoryDTO", menuCategoryDTO);
        model.addAttribute("id", id);

        return "pages/manager/menu_category/update";
    }

    @PostMapping("/menu_category/{id}/edit")
    public String edit(@Validated @ModelAttribute MenuCategoryDTO menuCategoryDTO, BindingResult bindingResult,
                       RedirectAttributes attributes, Model model, @PathVariable Integer id) throws IOException {
        MenuCategory menuCategory = menuCategoryService.findById(id);
        if (bindingResult.hasErrors()) {
            return "pages/manager/menu_category/update";
        }
        if (menuCategory == null) {
            model.addAttribute("successMessage", "không tìm thấy món ăn cần thay đổi");
            return "pages/manager/menu_category/update";
        }
        BeanUtils.copyProperties(menuCategoryDTO, menuCategory);
        menuCategory.setId(id);
        menuCategoryService.save(menuCategory);
        model.addAttribute("menuCategory", menuCategory);
        attributes.addFlashAttribute("successMessage", "cập nhật thành công");
        return "redirect:/manager/menu_category";


    }

    @PostMapping("/menu_category/{id}/delete")
    public String softDelete(@PathVariable("id") Integer id, RedirectAttributes attributes) {
        MenuCategory menuCategory = menuCategoryService.findById(id);
        if (menuCategory != null) {
            menuCategoryService.softDelete(id);
            attributes.addFlashAttribute("successMessage", "xóa thành công");
        } else {
            attributes.addFlashAttribute("successMessage", "xóa thất bại");

        }
        return "redirect:/manager/menu_category";
    }

}
