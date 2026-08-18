package com.portfolio.fund_tracking_api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class History {
    private String fundName;
    private Map<String, Double> shareValues;
}
