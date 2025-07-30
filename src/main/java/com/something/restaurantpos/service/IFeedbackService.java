package com.something.restaurantpos.service;

import com.something.restaurantpos.entity.Feedback;
import com.something.restaurantpos.entity.Invoice;

import java.util.List;

public interface IFeedbackService{
    boolean existsById(String id);
    List<Feedback> findAll();
    Feedback findById(String id);
    void save(Feedback feedback);
}
