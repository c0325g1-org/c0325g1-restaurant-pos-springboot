package com.something.restaurantpos.repository;

import com.something.restaurantpos.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IMenuItemRepository extends JpaRepository<MenuItem, Integer> {
    List<MenuItem> findByIsAvailableTrue();
}
