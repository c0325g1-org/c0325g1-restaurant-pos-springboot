package com.something.restaurantpos.service;

import com.something.restaurantpos.dto.MenuItemDTO;
import com.something.restaurantpos.entity.MenuItem;

import java.util.List;

public interface IMenuItemService {
    List<MenuItem> getAvailableItems();
    MenuItem getById(Integer id);
}
