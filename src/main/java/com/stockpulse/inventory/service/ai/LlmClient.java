package com.stockpulse.inventory.service.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class LlmClient {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${llm.api-key:}")
    private String apiKey;

    public String call(String prompt) {
        if (apiKey == null || apiKey.isBlank()) {
            return "";
        }
        return "";
    }

    public <T> T parseJson(String json, Class<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("AI response could not be parsed as JSON", ex);
        }
    }

    public Map<String, Object> parseJsonMap(String json) {
        try {
            return objectMapper.readValue(json, Map.class);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("AI response could not be parsed as JSON", ex);
        }
    }
}
