package com.portfolio.fund_tracking_api.Fund.service;

import com.portfolio.fund_tracking_api.Fund.model.Fund;
import com.portfolio.fund_tracking_api.Fund.model.Holding;
import com.portfolio.fund_tracking_api.Fund.persistance.FundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.InvalidAttributeValueException;
import java.util.Map;
import java.util.Set;

@Service
public class FundService {
    @Autowired public FundRepository repository;

    public Fund getComplete(String fundName) throws InvalidAttributeValueException {
        Fund fund = repository.read();

        if (!fund.getName().equals(fundName))
            throw new InvalidAttributeValueException("FUND NAMES DOESN'T MATCH");

        return fund;
    }

    public Set<Holding> getHoldings(String fundName) throws InvalidAttributeValueException {
        return getComplete(fundName).getHoldings();
    }

    public Map<String, Double> getBreakdown(String fundName) throws InvalidAttributeValueException {
        return getComplete(fundName).getBreakdown();
    }
}
