package com.something.restaurantpos.controller.manager;


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
public class MenuItemController {

    @Autowired
    private IMenuItemService menuItemService;
    @Autowired
    private IMenuCategoryService menuCategoryService;
    @Autowired
    private CloudinaryService cloudinaryService;
    private int size = 10;

    @ModelAttribute("menuCategoryList")
    public List<MenuCategory> findAllCategory() {
        return menuCategoryService.findAll();
    }

    @GetMapping("/menu_items")
    public String getAll(Model model,
                         @RequestParam(name = "page", defaultValue = "0") int page,
                         @RequestParam(defaultValue = "") String keyword,
                         @RequestParam(defaultValue = "") Integer categoryId) {

        int seachId;
        if (categoryId == null) {
            seachId = 0;
        } else {
            seachId = categoryId;
        }
        Page<MenuItem> menuItems = menuItemService.search(keyword, seachId, PageRequest.of(page, size,Sort.by(Sort.Direction.DESC, "id")));

        long trashCount = menuItemService.countDeleted();
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("categories", menuCategoryService.findAll());
        model.addAttribute("menuItems", menuItems);
        model.addAttribute("trashCount", trashCount);
        return "pages/manager/menu_item/list";
    }

    @GetMapping("/menu_items/trash")
    public String trash(@RequestParam(value = "page", defaultValue = "0") int page,
                        Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MenuItem> trashMenuItems = menuItemService.findTrash(pageable);
        model.addAttribute("menuItems", trashMenuItems);
        return "pages/manager/menu_item/trash";
    }

    @PostMapping("/menu_items/{id}/restore")
    public String restore(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        menuItemService.restore(id);
        redirectAttributes.addFlashAttribute("successMessage", "Khôi phục món ăn thành công!");
        return "redirect:/manager/menu_items/trash";
    }

    @PostMapping("/menu_items/{id}/destroy")
    public String destroy(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        menuItemService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Xóa vĩnh viễn món ăn thành công!");
        return "redirect:/manager/menu_items/trash";
    }

    @GetMapping("/menu_items/save")
    public String create(Model model) {
        model.addAttribute("menuItemDTO", new MenuItemDTO());
        return "pages/manager/menu_item/create";
    }

    @PostMapping("/menu_items/save")
    public String save(@Validated @ModelAttribute MenuItemDTO menuItemDTO,
                       BindingResult bindingResult, RedirectAttributes attribute) throws IOException {
        if (bindingResult.hasErrors()) {
            return "pages/manager/menu_item/create";
        }
        String imageUrl = null;

        // Nếu có ảnh crop (từ base64)
        if (menuItemDTO.getCroppedImage() != null && !menuItemDTO.getCroppedImage().isEmpty()) {
            String base64 = menuItemDTO.getCroppedImage();
            byte[] imageBytes = Base64.getDecoder().decode(base64.split(",")[1]);
            MultipartFile multipartFile = new MockMultipartFile("image", "cropped.jpg", "image/jpeg", imageBytes);

            // Upload ảnh đã crop
            imageUrl = cloudinaryService.uploadImage(multipartFile);
        } else {
            // Nếu không có ảnh crop, dùng ảnh gốc
            MultipartFile imageFile = menuItemDTO.getImageUrl();
            imageUrl = cloudinaryService.uploadImage(imageFile);
        }

        // Tạo đối tượng entity và copy dữ liệu
        MenuItem menuItem = new MenuItem();
        BeanUtils.copyProperties(menuItemDTO, menuItem);
        menuItem.setImageUrl(imageUrl); // ảnh đã upload
        menuItemService.save(menuItem);

        // Thông báo thành công
        attribute.addFlashAttribute("successMessage", "Tạo món ăn thành công");
        attribute.addFlashAttribute("menuItem", menuItem);

        return "redirect:/manager/menu_items";
    }

    @GetMapping("/menu_items/{id}/edit")
    public String update(@PathVariable Integer id, @ModelAttribute MenuItemDTO menuItemDTO, RedirectAttributes attributes, Model model) throws IOException {
        MenuItem menuItem = menuItemService.findById(id);
        if (menuItem == null) {
            attributes.addFlashAttribute("successMessage", "không tìm thấy id cần thay đổi");
            return "redirect:/menu_items";
        }
        menuItemDTO.setImageUrlString(menuItem.getImageUrl());
        BeanUtils.copyProperties(menuItem, menuItemDTO);
        model.addAttribute("menuItemDTO", menuItemDTO);
        model.addAttribute("id", id);

        return "pages/manager/menu_item/update";
    }

    @PostMapping("/menu_items/{id}/edit")
    public String edit(@Validated @ModelAttribute MenuItemDTO menuItemDTO, BindingResult bindingResult,
                       RedirectAttributes attributes, Model model, @PathVariable Integer id) throws IOException {
        MenuItem existingItem = menuItemService.findById(id);
        if (bindingResult.hasErrors()) {
            return "pages/manager/menu_item/update";
        }
        if (existingItem == null) {
         model.addAttribute("successMessage", "không tìm thấy món ăn cần thay đổi");
           return "pages/manager/menu_item/update";
        }
        if(menuItemDTO.getCroppedImage()!=null&& !menuItemDTO.getCroppedImage().isEmpty()){
            String base64= menuItemDTO.getCroppedImage();
            byte[] imgaBytes=Base64.getDecoder().decode(base64.split(",")[1]);
            MultipartFile multipartFile= new MockMultipartFile("image","cropped.jpg","image/jpeg",imgaBytes);
            String imageUrl=cloudinaryService.uploadImage(multipartFile);
            existingItem.setImageUrl(imageUrl);
        }else if(menuItemDTO.getImageUrl()!=null&&!menuItemDTO.getImageUrl().isEmpty()){
            String imageUrl=cloudinaryService.uploadImage(menuItemDTO.getImageUrl());
            existingItem.setImageUrl(imageUrl);
        }else {
            existingItem.setImageUrl(existingItem.getImageUrl());
        }


//        MultipartFile imageFile = menuItemDTO.getImageUrl();
//
//        if ((imageFile != null && !imageFile.isEmpty())) {
//            String imageUrl = cloudinaryService.uploadImage(imageFile);
//            menuItem.setImageUrl(imageUrl);
//        } else {
//            menuItem.setImageUrl(existingItem.getImageUrl());
//        }

        BeanUtils.copyProperties(menuItemDTO, existingItem);
        existingItem.setId(id);
        menuItemService.save(existingItem);
        model.addAttribute("menuItem", existingItem);
        attributes.addFlashAttribute("successMessage", "cập nhật thành công");
        return "redirect:/manager/menu_items";


    }
    @PostMapping("/menu_items/{menuItemId}/delete")
    public String softDelete(@PathVariable("menuItemId") Integer id, RedirectAttributes attributes) {
        MenuItem menuItem = menuItemService.findById(id);
        if (menuItem != null) {
            menuItemService.softDelete(id);
            attributes.addFlashAttribute("successMessage", "xóa thành công");
        } else {
            attributes.addFlashAttribute("successMessage", "xóa thất bại");

        }
        return "redirect:/manager/menu_items";
    }


}