package com.portfolio.fund_tracking_api.Fund.controller;

import com.portfolio.fund_tracking_api.Fund.model.Fund;
import com.portfolio.fund_tracking_api.Fund.model.Holding;
import com.portfolio.fund_tracking_api.Fund.service.FundService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.management.InvalidAttributeValueException;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FundController.class)
class FundControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FundService fundService;

    private Fund mockFund;
    private final String FUND_NAME = "Global Tech Growth Fund";

    @BeforeEach
    void setUp() {
        mockFund = new Fund(
                "Global Tech Growth Fund",
                "2026-08-15",
                2,
                Map.of(
                        "Technology", 40.5,
                        "Healthcare", 30.0,
                        "Finance", 29.5
                ),
                Set.of(
                        new Holding("Apple Inc.", 25.5),
                        new Holding("Microsoft Corp.", 15.0)
                )
        );
    }

    @Nested
    @DisplayName("GET /api/fund/{fundName}")
    class GetCompleteEndpointTests {

        @Test
        @DisplayName("Should return 200 OK and complete Fund object when found")
        void getComplete_ShouldReturn200AndFund_WhenValidName() throws Exception {
            when(fundService.getComplete(FUND_NAME)).thenReturn(mockFund);

            mockMvc.perform(get("/api/fund/{fundName}", FUND_NAME)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.name", is(FUND_NAME)))
                    .andExpect(jsonPath("$.totalHoldings", is(2)));

            verify(fundService, times(1)).getComplete(FUND_NAME);
        }

        @Test
        @DisplayName("Should return 400 Bad Request when fund name does not match")
        void getComplete_ShouldReturn400_WhenNameMismatch() throws Exception {
            when(fundService.getComplete("UnknownFund"))
                    .thenThrow(new InvalidAttributeValueException("FUND NAMES DOESN'T MATCH"));

            mockMvc.perform(get("/api/fund/{fundName}", "UnknownFund")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(fundService, times(1)).getComplete("UnknownFund");
        }
    }

    @Nested
    @DisplayName("GET /api/fund/holdings/{fundName}")
    class GetHoldingsEndpointTests {

        @Test
        @DisplayName("Should return 200 OK and set of holdings when found")
        void getHoldings_ShouldReturn200AndHoldings_WhenValidName() throws Exception {
            Set<Holding> mockHoldings = mockFund.getHoldings();
            when(fundService.getHoldings(FUND_NAME)).thenReturn(mockHoldings);

            mockMvc.perform(get("/api/fund/holdings/{fundName}", FUND_NAME)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)));

            verify(fundService, times(1)).getHoldings(FUND_NAME);
        }

        @Test
        @DisplayName("Should return 400 Bad Request when getting holdings for invalid name")
        void getHoldings_ShouldReturn400_WhenNameMismatch() throws Exception {
            when(fundService.getHoldings("InvalidName"))
                    .thenThrow(new InvalidAttributeValueException("FUND NAMES DOESN'T MATCH"));

            mockMvc.perform(get("/api/fund/holdings/{fundName}", "InvalidName")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(fundService, times(1)).getHoldings("InvalidName");
        }
    }

    @Nested
    @DisplayName("GET /api/fund/breakdown/{fundName}")
    class GetBreakdownEndpointTests {

        @Test
        @DisplayName("Should return 200 OK and breakdown map when found")
        void getBreakdown_ShouldReturn200AndBreakdown_WhenValidName() throws Exception {
            Map<String, Double> mockBreakdown = mockFund.getBreakdown();
            when(fundService.getBreakdown(FUND_NAME)).thenReturn(mockBreakdown);

            mockMvc.perform(get("/api/fund/breakdown/{fundName}", FUND_NAME)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.Technology", is(40.5)))
                    .andExpect(jsonPath("$.Healthcare", is(30.0)));

            verify(fundService, times(1)).getBreakdown(FUND_NAME);
        }

        @Test
        @DisplayName("Should return 400 Bad Request when getting breakdown for invalid name")
        void getBreakdown_ShouldReturn400_WhenNameMismatch() throws Exception {
            when(fundService.getBreakdown("InvalidName"))
                    .thenThrow(new InvalidAttributeValueException("FUND NAMES DOESN'T MATCH"));

            mockMvc.perform(get("/api/fund/breakdown/{fundName}", "InvalidName")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(fundService, times(1)).getBreakdown("InvalidName");
        }
    }
}