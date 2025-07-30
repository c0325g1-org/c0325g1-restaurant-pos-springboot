package com.something.restaurantpos.service;

import com.something.restaurantpos.dto.InvoiceDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IService <T> {
    List<T> findAll();
    T findById(Integer id);
    T findByIdOrThrow(Integer id);
    void save(T t);
    void deleteById(Integer id);
    Page<T> findTrash(Pageable pageable);
    void softDelete(Integer id);
    void restore(Integer id);
    long countDeleted();

}
