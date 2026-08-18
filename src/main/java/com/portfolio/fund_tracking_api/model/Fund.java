package com.portfolio.fund_tracking_api.model;

import lombok.*;

import java.util.Map;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Fund {
    private String name;
    private String date;
    private Integer totalHoldings;
    private Double shareValue;

    private Map<String, Double> breakdown;
    private Set<Holding> holdings;
}
