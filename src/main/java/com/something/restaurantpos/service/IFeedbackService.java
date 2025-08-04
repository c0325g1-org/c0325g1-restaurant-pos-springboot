package com.something.restaurantpos.service;

import com.something.restaurantpos.entity.Feedback;
import com.something.restaurantpos.entity.Invoice;
import com.something.restaurantpos.entity.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IFeedbackService{
    boolean existsById(String id);
    List<Feedback> findAll();
    Feedback findById(String id);
    void save(Feedback feedback);
    Page<Feedback> search(String name, Pageable pageable);
    void deleteMultipleByIds(List<String> ids);
    boolean existsIncludingDeleted(String id);
    Page<Feedback> searchDeleted(String keyword, Pageable pageable);
    void restoreById(String id);
    void destroyById(String id);
    long countByDeleted(boolean deleted);
}
