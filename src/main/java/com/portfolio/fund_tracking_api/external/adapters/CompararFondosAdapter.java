package com.portfolio.fund_tracking_api.external.adapters;

import com.portfolio.fund_tracking_api.model.Fund;
import com.portfolio.fund_tracking_api.model.History;
import com.portfolio.fund_tracking_api.model.Holding;
import com.portfolio.fund_tracking_api.external.dto.FundCompositionDTO;
import com.portfolio.fund_tracking_api.external.dto.FundHistoryDTO;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor
public class CompararFondosAdapter {
    private FundCompositionDTO composition;
    private FundHistoryDTO historyDTO;

    public CompararFondosAdapter setComposition(FundCompositionDTO composition) {
        this.composition = composition;
        return this;
    }

    public CompararFondosAdapter setHistoryDTO(FundHistoryDTO historyDTO) {
        this.historyDTO = historyDTO;
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

        FundHistoryDTO.HistoryEntryDTO historyEntries = this.historyDTO.getHistory().getLast();

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
        History historyEntity = new History();
        List<History.ShareValue> shareValues = new ArrayList<>();

        historyEntity.setFundName(this.historyDTO.getName());

        for (int i = 0; i < this.historyDTO.getHistory().size(); i++) {
            Double lastVariation = i == 0
                    ? 0.0
                    : this.historyDTO.getHistory().get(i - 1).getShareValue();

            Double currentVariation = this.historyDTO.getHistory().get(i).getShareValue();

            shareValues.add(
                    new History.ShareValue(
                            historyDTO.getHistory().get(i).getDate(),
                            historyDTO.getHistory().get(i).getShareValue(),
                            Math.floor(currentVariation - lastVariation)
                    )
            );
        }

        historyEntity.setShareValues(shareValues);

        return historyEntity;
    }
}
