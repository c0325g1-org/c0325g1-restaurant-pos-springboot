package com.something.restaurantpos.service;

import com.something.restaurantpos.entity.MenuCategory;
import com.something.restaurantpos.entity.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IMenuCategoryService extends IService<MenuCategory>{
     Page<MenuCategory> findAllCategory(Pageable pageable);
}
