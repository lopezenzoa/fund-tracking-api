package com.portfolio.fund_tracking_api.Fund.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Holding {
    private String name;
    private Double percentage;
}
