package com.portfolio.fund_tracking_api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class History {
    private String fundName;
    private List<ShareValue> shareValues;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShareValue {
        private String date;
        private Double shareValue;
        private Double variation;
    }
}
