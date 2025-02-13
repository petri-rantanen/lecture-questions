package com.utils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Tests for the OpenAIClient class.
 */
public class OpenAIClientTest {
    private OpenAIClient openAIClient;

    @Mock
    private HttpClient mockHttpClient;
    
    private HttpResponse<Object> mockResponse; // Remove @Mock and initialize manually

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        openAIClient = new OpenAIClient(mockHttpClient);

        // Explicitly mock the response to avoid NullPointerException
        mockResponse = mock(HttpResponse.class);
    }

    @Test
    void testGenerateTopics_Success() throws Exception {
        // Simulate a successful API response
        String jsonResponse = "{ \"choices\": [ { \"message\": { \"content\": \"Topic 1, Topic 2\" } } ] }";

        when(mockHttpClient.send(any(HttpRequest.class), any()))
            .thenReturn(mockResponse); 
        when(mockResponse.body()).thenReturn(jsonResponse);

        String result = openAIClient.generateTopics(List.of("What is cloud computing?"), "Lecture slides content");

        assertNotNull(result);
        assertTrue(result.contains("Topic 1"), "Should contain generated topics");
    }

    @Test
    void testGenerateTopics_APIError() throws Exception {
        when(mockHttpClient.send(any(HttpRequest.class), any()))
            .thenThrow(new IOException("Network Error"));

        String result = openAIClient.generateTopics(List.of("Question?"), "Context");

        assertNull(result, "Should return null on API error");
    }
}
