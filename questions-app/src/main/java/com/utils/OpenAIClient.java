package com.utils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * OpenAI client to generate topics based on questions.
 */
@ApplicationScoped
public class OpenAIClient {
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String FIELD_CHOICES = "choices";
    private static final String FIELD_MESSAGE = "message";
    private static final String FIELD_CONTENT = "content";
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(OpenAIClient.class);
    private static final String KEY_MODEL = "model";
    private static final String KEY_TEMPERATURE = "temperature";
    @ConfigProperty(name = "openapi.key")
    private String apiKey;
    @ConfigProperty(name = "openapi.model")
    private String model;
    @ConfigProperty(name = "openapi.temperature")
    private double temperature;
    @ConfigProperty(name = "openapi.topics")
    private int topicCount;
    private final HttpClient client;

    /**
     * Create a new OpenAI client.
     */
    public OpenAIClient() {
        client = HttpClient.newHttpClient();
    }

    /**
     * Create a new OpenAI client with the given HttpClient.
     * @param client
     */
    public OpenAIClient(HttpClient client) {
        this.client = client;
    }

    /**
     * Generate topics for the given questions.
     * @param questions
     * @param context
     * @return topics
     */
    public String generateTopics(List<String> questions, String context) {
        HashMap<String, Object> requestBody = new HashMap<>();
        requestBody.put(KEY_MODEL, model);
        ArrayList<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", "Your task is to create "+topicCount+" group discussion topics based on a list of questions, notes or ideas provided by students (user) on a lecture titled \"deploying containerized applications to the cloud in practice\". For each topic, also create three claims for a starting point on group discussion. At least one of the claims should be false, and one should be true. Do not include information on which claim is true and which one is false, it will be up to the students to figure out which ones are true. Always provide the topics in English."));
        messages.add(Map.of("role", "system", "content", "Try to stay within the scope of the lecture slides, because the students might not have knowledge of content not mentioned in the slides. The contents of the lecture slides are here: "+context));
        messages.add(Map.of("role", "user", "content", "Here is the list of questions: "+String.join(",",questions)));

        requestBody.put("messages", messages);
        requestBody.put(KEY_TEMPERATURE, temperature);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(API_URL))
            .header("Authorization", "Bearer " + apiKey)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(requestBody)))
            .build();

            // Send request and get response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return objectMapper.readTree(response.body()).path(FIELD_CHOICES).get(0).path(FIELD_MESSAGE).path(FIELD_CONTENT).asText();
        } catch (IOException | InterruptedException e) {
            LOGGER.debug("Failed to fetch topics from OpenAI API: {}", e.getMessage());
        }
        return null;
    }
}
