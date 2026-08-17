package com.portfolio.fund_tracking_api.Integrations.CompararFondos.service;

import com.portfolio.fund_tracking_api.Integrations.CompararFondos.dto.CompararFondosDTO;
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

    private final HttpClient httpClient;
    private final ObjectMapper mapper;
    private final String BASE_URL = "https://compararfondos.com.ar/api/";

    // Injected via Spring IoC
    public CompararFondosService(
            HttpClient httpClient,
            ObjectMapper mapper) {
        this.httpClient = httpClient;
        this.mapper = mapper;
    }

    // Single responsibility method that returns the DTO directly
    public CompararFondosDTO getFundComposition(String fundName) {
        try {
            HttpResponse<String> response = fetchFund(fundName);

            if (response.statusCode() != 200) {
                throw new RuntimeException("API error: Received status code " + response.statusCode());
            }

            return mapHttpResponse(response);
        } catch (IOException e) {
            throw new RuntimeException("Network error while calling CompararFondos API", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("HTTP request was interrupted", e);
        }
    }

    public HttpResponse<String> fetchFund(String fundName) throws IOException, InterruptedException {
        String encodedFundName = URLEncoder.encode(fundName, StandardCharsets.UTF_8);
        URI uri = URI.create(BASE_URL + "composicion/" + encodedFundName);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Accept", "application/json")
                .GET()
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public CompararFondosDTO mapHttpResponse(HttpResponse<String> response) {
        try {
            return mapper.readValue(response.body(), CompararFondosDTO.class);
        } catch (JacksonException e) {
            throw new RuntimeException("Failed to deserialize fund response", e);
        }
    }
}