package com.portfolio.fund_tracking_api.Integrations.CompararFondos.service;

import com.portfolio.fund_tracking_api.Integrations.CompararFondos.dto.CompararFondosDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompararFondosServiceTest {

    @Mock
    private HttpClient httpClient;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private HttpResponse<String> httpResponse;

    private CompararFondosService service;
    private final String BASE_URL = "https://test-api.com/api/";

    @BeforeEach
    void setUp() {
        service = new CompararFondosService(httpClient, mapper, BASE_URL);
    }

    @Test
    @DisplayName("fetchFund should construct correct HTTP request and delegate to HttpClient")
    void fetchFund_ShouldSendCorrectRequest() throws IOException, InterruptedException {
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);

        HttpResponse<String> result = service.fetchFund("Fund Name");

        assertNotNull(result);

        // Verify request construction details
        ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient).send(requestCaptor.capture(), any(HttpResponse.BodyHandler.class));

        HttpRequest capturedRequest = requestCaptor.getValue();
        assertEquals("GET", capturedRequest.method());
        assertEquals("https://test-api.com/api/composicion/Fund+Name", capturedRequest.uri().toString());
        assertEquals("application/json", capturedRequest.headers().firstValue("Accept").orElse(""));
    }

    @Test
    @DisplayName("mapHttpResponse should use ObjectMapper to parse response body")
    void mapHttpResponse_ShouldReturnDTO() throws IOException {
        String jsonBody = "{\"name\":\"Test Fund\"}";
        CompararFondosDTO expectedDto = new CompararFondosDTO();

        when(httpResponse.body()).thenReturn(jsonBody);
        when(mapper.readValue(jsonBody, CompararFondosDTO.class)).thenReturn(expectedDto);

        CompararFondosDTO actualDto = service.mapHttpResponse(httpResponse);

        System.out.println(actualDto);

        assertNotNull(actualDto);
        assertEquals(expectedDto, actualDto);
        verify(mapper, times(1)).readValue(jsonBody, CompararFondosDTO.class);
    }
}