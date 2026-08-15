package com.portfolio.fund_tracking_api.Fund.service;

import com.portfolio.fund_tracking_api.Fund.model.Fund;
import com.portfolio.fund_tracking_api.Fund.model.Holding;
import com.portfolio.fund_tracking_api.Fund.persistance.FundRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.management.InvalidAttributeValueException;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FundServiceTest {

    @Mock
    private FundRepository repository;

    @InjectMocks
    private FundService fundService;

    private Fund mockFund;
    private final String VALID_FUND_NAME = "Global Tech Growth Fund";

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
    @DisplayName("getComplete Tests")
    class GetCompleteTests {

        @Test
        @DisplayName("Should return complete Fund when fund names match")
        void getComplete_ShouldReturnFund_WhenNamesMatch() throws InvalidAttributeValueException {
            when(repository.read()).thenReturn(mockFund);

            Fund result = fundService.getComplete(VALID_FUND_NAME);

            assertNotNull(result);
            assertEquals(VALID_FUND_NAME, result.getName());
            verify(repository, times(1)).read();
        }

        @Test
        @DisplayName("Should throw InvalidAttributeValueException when fund names do not match")
        void getComplete_ShouldThrowException_WhenNamesDoNotMatch() {
            when(repository.read()).thenReturn(mockFund);

            InvalidAttributeValueException exception = assertThrows(
                    InvalidAttributeValueException.class,
                    () -> fundService.getComplete("Mismatched Fund Name")
            );

            assertEquals("FUND NAMES DOESN'T MATCH", exception.getMessage());
            verify(repository, times(1)).read();
        }

        @Test
        @DisplayName("Should propagate RuntimeException when repository read fails")
        void getComplete_ShouldPropagateException_WhenRepositoryFails() {
            when(repository.read()).thenThrow(new RuntimeException("File read error"));

            assertThrows(RuntimeException.class, () -> fundService.getComplete(VALID_FUND_NAME));
            verify(repository, times(1)).read();
        }
    }

    @Nested
    @DisplayName("getHoldings Tests")
    class GetHoldingsTests {

        @Test
        @DisplayName("Should return Holdings set when fund name matches")
        void getHoldings_ShouldReturnHoldingsSet_WhenNamesMatch() throws InvalidAttributeValueException {
            when(repository.read()).thenReturn(mockFund);

            Set<Holding> holdings = fundService.getHoldings(VALID_FUND_NAME);

            assertNotNull(holdings);
            assertEquals(2, holdings.size());
            verify(repository, times(1)).read();
        }

        @Test
        @DisplayName("Should throw InvalidAttributeValueException when getting holdings with mismatched name")
        void getHoldings_ShouldThrowException_WhenNamesDoNotMatch() {
            when(repository.read()).thenReturn(mockFund);

            assertThrows(
                    InvalidAttributeValueException.class,
                    () -> fundService.getHoldings("Wrong Name")
            );
        }
    }

    @Nested
    @DisplayName("getBreakdown Tests")
    class GetBreakdownTests {

        @Test
        @DisplayName("Should return Breakdown map when fund name matches")
        void getBreakdown_ShouldReturnBreakdownMap_WhenNamesMatch() throws InvalidAttributeValueException {
            when(repository.read()).thenReturn(mockFund);

            Map<String, Double> breakdown = fundService.getBreakdown(VALID_FUND_NAME);

            assertNotNull(breakdown);
            assertEquals(3, breakdown.size());
            assertTrue(breakdown.containsKey("Technology"));
            verify(repository, times(1)).read();
        }

        @Test
        @DisplayName("Should throw InvalidAttributeValueException when getting breakdown with mismatched name")
        void getBreakdown_ShouldThrowException_WhenNamesDoNotMatch() {
            when(repository.read()).thenReturn(mockFund);

            assertThrows(
                    InvalidAttributeValueException.class,
                    () -> fundService.getBreakdown("Wrong Name")
            );
        }
    }
}