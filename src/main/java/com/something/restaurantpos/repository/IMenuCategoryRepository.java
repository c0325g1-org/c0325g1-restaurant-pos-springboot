package com.something.restaurantpos.repository;

import com.something.restaurantpos.entity.MenuCategory;
import com.something.restaurantpos.entity.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IMenuCategoryRepository extends JpaRepository<MenuCategory,Integer> {
    long countByDeletedTrue();
    Page<MenuCategory> findByDeletedTrue(Pageable pageable);
}
