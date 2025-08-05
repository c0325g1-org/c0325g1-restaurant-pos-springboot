package com.something.restaurantpos.service.impl;

import com.something.restaurantpos.entity.Feedback;
import com.something.restaurantpos.repository.IFeedbackRepository;
import com.something.restaurantpos.service.IFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackService implements IFeedbackService {

    @Autowired
    private IFeedbackRepository feedbackRepository;

    @Override
    public boolean existsById(String id) {
        return feedbackRepository.existsById(id);
    }

    @Override
    public List<Feedback> findAll() {
        return feedbackRepository.findAll();
    }

    @Override
    public Feedback findById(String id) {
        return feedbackRepository.findById(id).orElse(null);
    }

    @Override
    public void save(Feedback feedback) {
        feedbackRepository.save(feedback);
    }

    @Override
    public Page<Feedback> search(String name, Pageable pageable) {
        return feedbackRepository.search(name, pageable);
    }

    @Override
    public void deleteMultipleByIds(List<String> ids) {
        for (String id : ids) {
            Feedback feedback = feedbackRepository.findById(id).orElse(null);
            if (feedback != null) {
                feedback.markDeleted();
                feedbackRepository.save(feedback);
            }
        }
    }

    @Override
    public boolean existsIncludingDeleted(String id) {
        return feedbackRepository.existsIncludingDeleted(id);
    }

    @Override
    public Page<Feedback> searchDeleted(String keyword, Pageable pageable) {
        return feedbackRepository.searchDeleted(keyword, pageable);
    }

    @Override
    public void restoreById(String id) {
        Feedback feedback = feedbackRepository.findById(id).orElse(null);
        if (feedback != null) {
            feedback.restore();
            feedbackRepository.save(feedback);
        }
    }

    @Override
    public void destroyById(String id) {
        feedbackRepository.deleteById(id); // Xoá vĩnh viễn khỏi DB
    }

    @Override
    public long countByDeleted(boolean deleted) {
        return feedbackRepository.countByDeleted(true);
    }



    @Override
    public List<Feedback> findTop5FiveStarFeedbacks(Pageable pageable) {
        return feedbackRepository.findTop5FiveStarFeedbacks(pageable);
    }


}
