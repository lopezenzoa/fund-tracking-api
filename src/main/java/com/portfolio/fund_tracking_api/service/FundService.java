package com.portfolio.fund_tracking_api.service;

import com.portfolio.fund_tracking_api.model.Fund;
import com.portfolio.fund_tracking_api.model.History;
import com.portfolio.fund_tracking_api.model.Holding;
import com.portfolio.fund_tracking_api.persistance.FundRepository;
import com.portfolio.fund_tracking_api.external.adapters.CompararFondosAdapter;
import com.portfolio.fund_tracking_api.external.dto.FundCompositionDTO;
import com.portfolio.fund_tracking_api.external.dto.FundHistoryDTO;
import com.portfolio.fund_tracking_api.external.service.CompararFondosService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

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

           // Searching in local history (a.k.a Cache)
           Optional<Fund> fundOptional = repository.findByName(fundName);

           if (fundOptional.isPresent())
               return fundOptional.get();

           // Get Composition
           FundCompositionDTO composition = externalService.getFundComposition(fundName);

           // Get Fund History
           FundHistoryDTO fundHistory = externalService.getFundHistory(fundName);

           Fund fund = new CompararFondosAdapter()
                   .setComposition(composition)
                   .setHistory(fundHistory)
                   .adaptToFund();

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

    public History getShareValueHistory(String fundName, String fromDate) {
        // Fetch from CompararFondos API the complete Fund History of share values
        FundHistoryDTO fundHistory = externalService.getFundHistory(fundName);

        // Filter share values between today and date parameter
        List<FundHistoryDTO.HistoryEntryDTO> filteredHistory = fundHistory.getHistory().stream()
                .filter(historyEntry ->
                        ChronoUnit.DAYS.between(
                                LocalDate.parse(fromDate),
                                LocalDate.parse(historyEntry.getDate())
                        ) > 0
                )
                .toList();

        // Set the HistoryDTO with the filtered list
        fundHistory.setHistory(filteredHistory);

        // Adapt from HistoryDTO to History
        return new CompararFondosAdapter()
                .setHistory(fundHistory)
                .adaptToHistory();
    }
}
