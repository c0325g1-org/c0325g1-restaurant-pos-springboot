package com.something.restaurantpos.service.impl;

import com.something.restaurantpos.entity.MenuCategory;
import com.something.restaurantpos.repository.IMenuCategoryRepository;
import com.something.restaurantpos.service.IMenuCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuCategoryService implements IMenuCategoryService {

    private final IMenuCategoryRepository menuCategoryRepository;

    @Override
    public List<MenuCategory> findAll() {
        return menuCategoryRepository.findAll();
    }
}
