package com.portfolio.fund_tracking_api.external.adapters;

import com.portfolio.fund_tracking_api.model.Fund;
import com.portfolio.fund_tracking_api.model.History;
import com.portfolio.fund_tracking_api.model.Holding;
import com.portfolio.fund_tracking_api.external.dto.FundCompositionDTO;
import com.portfolio.fund_tracking_api.external.dto.FundHistoryDTO;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
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

    public Fund adaptToFund() {
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
                    .sorted(Comparator.comparingDouble(Holding::getPercentage).reversed())
                    .collect(Collectors.toCollection(LinkedHashSet::new))
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

    public History adaptToHistory() {
        History history = new History();

        history.setFundName(this.history.getName());

        // Map of share values
        Map<String, Double> shareValues = new TreeMap<>();

        this.history.getHistory()
                .forEach(historyEntry -> shareValues.put(
                        historyEntry.getDate(), historyEntry.getShareValue()
                ));

        // Sort the Map
        Map<String, Double> sortedMap = shareValues.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        TreeMap::new // Maintains keys in ascending date order
                ));

        history.setShareValues(sortedMap);

        return history;
    }
}
