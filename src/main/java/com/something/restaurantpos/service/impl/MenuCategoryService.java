package com.something.restaurantpos.service.impl;

import com.something.restaurantpos.entity.MenuCategory;
import com.something.restaurantpos.entity.MenuItem;
import com.something.restaurantpos.repository.IMenuCategoryRepository;
import com.something.restaurantpos.service.IMenuCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuCategoryService implements IMenuCategoryService {
    @Autowired
    private IMenuCategoryRepository menuCategoryRepository;

    @Override
    public List<MenuCategory> findAll() {
        return menuCategoryRepository.findAll();
    }

    @Override
    public MenuCategory findById(Integer id) {
        return menuCategoryRepository.findById(id).orElse(null);
    }

    @Override
    public MenuCategory findByIdOrThrow(Integer id) {
        return menuCategoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy loại món có ID = " + id));
    }

    @Override
    public void save(MenuCategory menuCategory) {
        menuCategoryRepository.save(menuCategory);
    }

    @Override
    public void deleteById(Integer id) {
        menuCategoryRepository.deleteById(id);
    }

    @Override
    public Page<MenuCategory> findTrash(Pageable pageable) {
        return menuCategoryRepository.findByDeletedTrue(pageable);    }

    @Override
    public void softDelete(Integer id) {
        MenuCategory menuCategory = findById(id);
        menuCategory.setDeleted(true);
        menuCategoryRepository.save(menuCategory);
    }

    @Override
    public void restore(Integer id) {
        MenuCategory menuCategory = findByIdOrThrow(id);
        menuCategory.restore();
        menuCategoryRepository.save(menuCategory);
    }

    @Override
    public long countDeleted() {
        return menuCategoryRepository.countByDeletedTrue();
    }

    @Override
    public Page<MenuCategory> findAllCategory(Pageable pageable) {
        return menuCategoryRepository.findAll(pageable);
    }

}
