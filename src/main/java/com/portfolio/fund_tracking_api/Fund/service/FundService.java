package com.portfolio.fund_tracking_api.Fund.service;

import com.portfolio.fund_tracking_api.Fund.model.Fund;
import com.portfolio.fund_tracking_api.Fund.model.Holding;
import com.portfolio.fund_tracking_api.Fund.persistance.FundRepository;
import com.portfolio.fund_tracking_api.Integrations.CompararFondos.adapter.CompararFondosAdapter;
import com.portfolio.fund_tracking_api.Integrations.CompararFondos.dto.FundCompositionDTO;
import com.portfolio.fund_tracking_api.Integrations.CompararFondos.dto.FundHistoryDTO;
import com.portfolio.fund_tracking_api.Integrations.CompararFondos.service.CompararFondosService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
@Getter
@AllArgsConstructor
public class FundService {
    private final FundRepository repository;
    private final CompararFondosService externalService;

    public Fund getComplete(String fundName) throws IllegalArgumentException {
       try {
           if (fundName == null || fundName.isBlank())
               throw new IllegalArgumentException("Fund Name Undefined");

           // Get Composition
           FundCompositionDTO composition = externalService.getFundComposition(fundName);

           // Get Fund History
           FundHistoryDTO fundHistory = externalService.getFundHistory(fundName);

           Fund fund = new CompararFondosAdapter()
                   .setComposition(composition)
                   .setHistory(fundHistory)
                   .map();

           if (!fund.getName().equals(fundName))
               throw new IllegalArgumentException("Fund Name Not Found");

           repository.save(fund);

           return fund;
       } catch (IllegalArgumentException e) {
           throw new IllegalArgumentException(e);
       }
    }

    public Set<Holding> getHoldings(String fundName) {
        return getComplete(fundName).getHoldings();
    }

    public Map<String, Double> getBreakdown(String fundName) {
        return getComplete(fundName).getBreakdown();
    }
}
