package com.portfolio.fund_tracking_api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FundVariation {
    private String date;
    private Double shareValue;
    private Double variation;
}
