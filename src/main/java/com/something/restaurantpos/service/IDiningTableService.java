package com.something.restaurantpos.service;

import com.something.restaurantpos.entity.DiningTable;

import java.util.List;

public interface IDiningTableService {
    List<DiningTable> findAvailableTables();
    DiningTable findById(Integer id);
    List<DiningTable> findAll();
    DiningTable save(DiningTable table);
}
