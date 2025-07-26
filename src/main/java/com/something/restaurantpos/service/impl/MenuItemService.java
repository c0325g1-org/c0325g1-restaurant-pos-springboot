package com.something.restaurantpos.service.impl;

import com.something.restaurantpos.entity.MenuItem;
import com.something.restaurantpos.repository.IMenuItemRepository;
import com.something.restaurantpos.service.IMenuItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuItemService implements IMenuItemService {

    private final IMenuItemRepository menuItemRepository;

    @Override
    public List<MenuItem> getAvailableItems() {
        return menuItemRepository.findByIsAvailableTrue();
    }

    @Override
    public MenuItem getById(Integer id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy món ăn"));
    }
}
