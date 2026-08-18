package com.portfolio.fund_tracking_api.persistance;

import com.portfolio.fund_tracking_api.model.Fund;

import java.util.List;
import java.util.Optional;

public interface FundRepository {
    void save(Fund fund);
    List<Fund> findAll();
    Optional<Fund> findByName(String name);
}
