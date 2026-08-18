package com.portfolio.fund_tracking_api.external.service;

import com.portfolio.fund_tracking_api.external.dto.FundCompositionDTO;
import com.portfolio.fund_tracking_api.external.dto.FundHistoryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Component
public class CompararFondosService {

    @Autowired private HttpClient httpClient;
    @Autowired private ObjectMapper mapper;
    private final String BASE_URL = "https://compararfondos.com.ar/api/";

    // Single responsibility method that returns the DTO directly
    public FundCompositionDTO getFundComposition(String fundName) {
        try {
            HttpResponse<String> response = fetchFundComposition(fundName);

            if (response.statusCode() != 200) {
                throw new RuntimeException("API error: Received status code " + response.statusCode());
            }

            return mapHttpResponseToFundCompositionDTO(response);
        } catch (IOException e) {
            throw new RuntimeException("Network error while calling CompararFondos API", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("HTTP request was interrupted", e);
        }
    }

    public FundHistoryDTO getFundHistory(String fundName) {
        try {
            HttpResponse<String> response = fetchFundHistory(fundName);

            if (response.statusCode() != 200) {
                throw new RuntimeException("API error: Received status code " + response.statusCode());
            }

            return mapHttpResponseToFundHistoryDTO(response);
        } catch (IOException e) {
            throw new RuntimeException("Network error while calling CompararFondos API", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("HTTP request was interrupted", e);
        }
    }

    private HttpResponse<String> fetchFundComposition(String fundName) throws IOException, InterruptedException {
        String encodedFundName = URLEncoder.encode(fundName, StandardCharsets.UTF_8);
        URI uri = URI.create(BASE_URL + "composicion/" + encodedFundName);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Accept", "application/json")
                .GET()
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> fetchFundHistory(String fundName) throws IOException, InterruptedException {
        String completeName = fundName + " - Clase A";
        String encodedFundName = URLEncoder.encode(completeName, StandardCharsets.UTF_8)
                .replace("+", "%20");

        URI uri = URI.create(BASE_URL + "fondo/" + encodedFundName);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Accept", "application/json")
                .GET()
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private FundCompositionDTO mapHttpResponseToFundCompositionDTO(HttpResponse<String> response) {
        try {
            return mapper.readValue(response.body(), FundCompositionDTO.class);
        } catch (JacksonException e) {
            throw new RuntimeException("Failed to deserialize fund response", e);
        }
    }

    private FundHistoryDTO mapHttpResponseToFundHistoryDTO(HttpResponse<String> response) {
        try {
            return mapper.readValue(response.body(), FundHistoryDTO.class);
        } catch (JacksonException e) {
            throw new RuntimeException("Failed to deserialize fund response", e);
        }
    }
}