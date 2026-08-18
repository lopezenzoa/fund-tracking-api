package com.portfolio.fund_tracking_api.external.adapters;

import com.portfolio.fund_tracking_api.model.Fund;
import com.portfolio.fund_tracking_api.model.Holding;
import com.portfolio.fund_tracking_api.external.dto.FundCompositionDTO;
import com.portfolio.fund_tracking_api.external.dto.FundHistoryDTO;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor
public class CompararFondosAdapter {
    private FundCompositionDTO composition;
    private FundHistoryDTO history;

    public CompararFondosAdapter setComposition(FundCompositionDTO composition) {
        this.composition = composition;
        return this;
    }

    public CompararFondosAdapter setHistory(FundHistoryDTO history) {
        this.history = history;
        return this;
    }

    public Fund map() {
        FundCompositionDTO.CompositionDTO composition = this.composition.getComposition();

        Map<String, Double> breakdownMap = composition.getBreakdown() != null
                ? composition.getBreakdown().stream()
                .collect(Collectors.toMap(
                        FundCompositionDTO.BreakdownItemDTO::getCategory,
                        FundCompositionDTO.BreakdownItemDTO::getPercentage,
                        (existing, replacement) -> existing
                ))
                : Collections.emptyMap();

        Set<Holding> holdings = composition.getHoldings() != null
                ? composition.getHoldings().stream()
                .map(h -> new Holding(h.getName(), h.getPercentage()))
                .collect(Collectors.toSet())
                : Collections.emptySet();

        FundHistoryDTO.HistoryEntryDTO historyEntries = this.history.getHistory().getLast();

        return new Fund(
                composition.getBaseName(),
                composition.getDate(),
                composition.getTotalHoldings(),
                historyEntries.getShareValue(),
                breakdownMap,
                holdings
        );
    }
}
