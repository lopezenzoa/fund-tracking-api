package com.portfolio.fund_tracking_api.external.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class FundCompositionDTO {

    @JsonProperty("status")
    private String status;

    @JsonProperty("composicion")
    private CompositionDTO composition;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @ToString
    public static class CompositionDTO {
        @JsonProperty("nombreBase")
        private String baseName;

        @JsonProperty("scrapedAt")
        private String date;

        @JsonProperty("totalHoldings")
        private Integer totalHoldings;

        @JsonProperty("breakdown")
        private List<BreakdownItemDTO> breakdown;

        @JsonProperty("holdings")
        private Set<HoldingDTO> holdings;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @ToString
    public static class BreakdownItemDTO {
        @JsonProperty("cat")
        private String category;

        @JsonProperty("pct")
        private Double percentage;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @ToString
    public static class HoldingDTO {
        @JsonProperty("activo")
        private String name;

        @JsonProperty("pct")
        private Double percentage;
    }
}
