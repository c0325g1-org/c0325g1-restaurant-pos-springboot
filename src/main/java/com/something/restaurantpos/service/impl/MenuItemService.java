package com.something.restaurantpos.service.impl;

import com.something.restaurantpos.entity.MenuItem;
import com.something.restaurantpos.repository.IMenuItemRepository;
import com.something.restaurantpos.service.IMenuItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuItemService implements IMenuItemService {

    private final IMenuItemRepository menuItemRepository;

    @Override
    public List<MenuItem> findAll() {
        return menuItemRepository.findAll();
    }


    @Override
    public MenuItem findById(Integer id) {
        return menuItemRepository.findById(id).orElse(null);
    }

    @Override
    public MenuItem findByIdOrThrow(Integer id) {
        return menuItemRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy món ăn có ID = " + id));
    }

    @Override
    public void save(MenuItem menuItem) {
        menuItemRepository.save(menuItem);
    }

    @Override
    public void deleteById(Integer id) {
        menuItemRepository.deleteById(id);
    }



    @Override
    public Page<MenuItem> findTrash(Pageable pageable) {
        return menuItemRepository.findByDeletedTrue(pageable);
    }


    @Override
    public void softDelete(Integer id) {
        MenuItem menuItem = findById(id);
        menuItem.setDeleted(true);
        menuItemRepository.save(menuItem);

    }


    @Override
    public Page<MenuItem> search(String name, Integer idCategory, Pageable pageable) {
        return menuItemRepository.search(name, idCategory, pageable);
    }

    @Override
    public List<MenuItem> findMenuItemOrderByTotalQuantityDesc() {
        return menuItemRepository.findMenuItemOrderByTotalQuantityDesc();
    }

    @Override
    public void restore(Integer id) {
        MenuItem menuItem = findByIdOrThrow(id);
        menuItem.restore();
        menuItemRepository.save(menuItem);
    }

    @Override
    public long countDeleted() {
        return menuItemRepository.countByDeletedTrue();
    }

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
