package com.example.demo.ai.converters;

import com.example.demo.entities.Sightseeing;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

@Component
public class SightseeingStructuredOutputConverter {

    private final ObjectMapper objectMapper;

    public SightseeingStructuredOutputConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String getFormat() {
        return """
            Return the response as a valid JSON object with the following structure:
            {
              "city": "string",
              "places": ["place1", "place2", "place3"],
              "description": "string",
              "bestTimeToVisit": "string",
              "averageCost": number
            }
            """;
    }

    public Sightseeing convertResponse(String responseContent) throws IOException {
        // Remove markdown code blocks if present
        String cleanedContent = responseContent
            .replaceAll("```json\\n?", "")
            .replaceAll("```\\n?", "")
            .trim();

        return objectMapper.readValue(cleanedContent, Sightseeing.class);
    }
}
