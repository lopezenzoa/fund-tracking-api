package com.portfolio.fund_tracking_api.external.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class FundHistoryDTO {
    @JsonProperty("nombre") private String name;
    private Integer count;
    private List<HistoryEntryDTO> history;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class HistoryEntryDTO {
        @JsonProperty("fecha") private String date;
        @JsonProperty("vcp") private Double shareValue;
    }
}
