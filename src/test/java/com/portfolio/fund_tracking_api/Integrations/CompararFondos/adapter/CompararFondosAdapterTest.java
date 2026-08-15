package com.portfolio.fund_tracking_api.Integrations.CompararFondos.adapter;

import com.portfolio.fund_tracking_api.Fund.model.Fund;
import com.portfolio.fund_tracking_api.Fund.model.Holding;
import com.portfolio.fund_tracking_api.Integrations.CompararFondos.dto.CompararFondosDTO;
import com.portfolio.fund_tracking_api.Integrations.CompararFondos.service.CompararFondosService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.net.http.HttpClient;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CompararFondosAdapterTest {

    @Nested
    @DisplayName("Happy Path Mapping Tests")
    class HappyPathTests {

        @Test
        @DisplayName("Should correctly map complete CompararFondosDTO to Fund domain model")
        void mapToFund_ShouldMapAllFieldsCorrectly() {
            // Arrange
            CompararFondosDTO dto = new CompararFondosDTO();
            CompararFondosDTO.CompositionDTO composition = new CompararFondosDTO.CompositionDTO();
            composition.setBaseName("Cocos Acciones");
            composition.setDate("2026-08-13T16:43:27.393Z");
            composition.setTotalHoldings(17);

            CompararFondosDTO.BreakdownItemDTO item1 = new CompararFondosDTO.BreakdownItemDTO();
            item1.setCategory("Participaciones Accionarias Argentina");
            item1.setPercentage(97.74);

            CompararFondosDTO.HoldingDTO holding1 = new CompararFondosDTO.HoldingDTO();
            holding1.setName("YPF S.A.");
            holding1.setPercentage(6.65);

            composition.setBreakdown(List.of(item1));
            composition.setHoldings(Set.of(holding1));
            dto.setComposition(composition);

            // Act
            Fund fund = CompararFondosAdapter.mapToFund(dto);

            // Assert
            assertNotNull(fund);
            assertEquals("Cocos Acciones", fund.getName());
            assertEquals("2026-08-13T16:43:27.393Z", fund.getDate());
            assertEquals(17, fund.getTotalHoldings());

            // Assert breakdown transformation (List -> Map)
            assertEquals(1, fund.getBreakdown().size());
            assertEquals(97.74, fund.getBreakdown().get("Participaciones Accionarias Argentina"));

            // Assert holdings transformation (HoldingDTO -> Holding)
            assertEquals(1, fund.getHoldings().size());
            Holding mappedHolding = fund.getHoldings().iterator().next();
            assertEquals("YPF S.A.", mappedHolding.getName());
            assertEquals(6.65, mappedHolding.getPercentage());
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should return null when DTO or composition is null")
        void mapToFund_ShouldReturnNull_WhenInputIsNull() {
            assertNull(CompararFondosAdapter.mapToFund(null));

            CompararFondosDTO emptyDto = new CompararFondosDTO();
            assertNull(CompararFondosAdapter.mapToFund(emptyDto));
        }

        @Test
        @DisplayName("Should initialize empty collections when breakdown or holdings are null")
        void mapToFund_ShouldHandleNullCollections() {
            CompararFondosDTO dto = new CompararFondosDTO();
            CompararFondosDTO.CompositionDTO composition = new CompararFondosDTO.CompositionDTO();
            composition.setBaseName("Empty Fund");
            dto.setComposition(composition);

            Fund fund = CompararFondosAdapter.mapToFund(dto);

            assertNotNull(fund);
            assertNotNull(fund.getBreakdown());
            assertTrue(fund.getBreakdown().isEmpty());
            assertNotNull(fund.getHoldings());
            assertTrue(fund.getHoldings().isEmpty());
        }
    }
}