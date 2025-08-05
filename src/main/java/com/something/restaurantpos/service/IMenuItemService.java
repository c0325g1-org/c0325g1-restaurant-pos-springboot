package com.something.restaurantpos.service;

import com.something.restaurantpos.entity.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IMenuItemService extends IService<MenuItem> {
    List<MenuItem> getAvailableItems();
    MenuItem getById(Integer id);
    Page<MenuItem> search(String name, Integer idCategory, Pageable pageable);
    List<MenuItem> findMenuItemOrderByTotalQuantityDesc();
}
