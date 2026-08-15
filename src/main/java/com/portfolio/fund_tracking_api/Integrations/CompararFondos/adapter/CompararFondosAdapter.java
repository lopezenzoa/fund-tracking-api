package com.portfolio.fund_tracking_api.Integrations.CompararFondos.adapter;

import com.portfolio.fund_tracking_api.Fund.model.Fund;
import com.portfolio.fund_tracking_api.Fund.model.Holding;
import com.portfolio.fund_tracking_api.Integrations.CompararFondos.dto.CompararFondosDTO;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CompararFondosAdapter {
    public static Fund mapToFund(CompararFondosDTO dto) {
        if (dto == null || dto.getComposition() == null) {
            return null;
        }

        CompararFondosDTO.CompositionDTO composition = dto.getComposition();

        Map<String, Double> breakdownMap = composition.getBreakdown() != null
                ? composition.getBreakdown().stream()
                .collect(Collectors.toMap(
                        CompararFondosDTO.BreakdownItemDTO::getCategory,
                        CompararFondosDTO.BreakdownItemDTO::getPercentage,
                        (existing, replacement) -> existing
                ))
                : Collections.emptyMap();

        Set<Holding> holdings = composition.getHoldings() != null
                ? composition.getHoldings().stream()
                .map(h -> new Holding(h.getName(), h.getPercentage()))
                .collect(Collectors.toSet())
                : Collections.emptySet();

        return new Fund(
                composition.getBaseName(),
                composition.getDate(),
                composition.getTotalHoldings(),
                breakdownMap,
                holdings
        );
    }
}
