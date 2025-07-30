package com.something.restaurantpos.service.impl;

import com.something.restaurantpos.entity.DiningTable;
import com.something.restaurantpos.repository.IDiningTableRepository;
import com.something.restaurantpos.service.IDiningTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiningTableService implements IDiningTableService {

    private final IDiningTableRepository diningTableRepository;

    @Override
    public List<DiningTable> findAll() {
        return diningTableRepository.findAll();
    }

    @Override
    public List<DiningTable> findAvailableTables() {
        return diningTableRepository.findByStatusIn(
                List.of(DiningTable.TableStatus.EMPTY, DiningTable.TableStatus.RESERVED)
        );
    }

    @Override
    public DiningTable findById(Integer id) {
        return diningTableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bàn"));
    }

    @Override
    public DiningTable save(DiningTable table) {
        return diningTableRepository.save(table);
    }
}
