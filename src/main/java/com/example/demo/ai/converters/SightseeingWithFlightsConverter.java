package com.example.demo.ai.converters;

import com.example.demo.entities.SightseeingInfo;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;

@Component
public class SightseeingWithFlightsConverter {

    private final ObjectMapper objectMapper;

    public SightseeingWithFlightsConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String getFormat() {
        return """
            Return the response as a valid JSON ARRAY with the following structure:
            [
              {
                "city": "string",
                "places": ["place1", "place2", "place3"],
                "description": "string",
                "outgoingFlights": [
                  {
                    "airline": "string",
                    "flightNo": "string",
                    "departureCity": "string",
                    "destinationCity": "string",
                    "departureDate": "yyyy-MM-dd'T'HH:mm:ss",
                    "arrivalDate": "yyyy-MM-dd'T'HH:mm:ss",
                    "price": number
                  }
                ]
              }
            ]
            """;
    }

    public SightseeingInfo convertResponse(String responseContent) throws IOException {
        // Remove markdown code blocks if present
        String cleanedContent = responseContent
            .replaceAll("```json\\n?", "")
            .replaceAll("```\\n?", "")
            .trim();

        return objectMapper.readValue(cleanedContent, SightseeingInfo.class);
    }

    public List<SightseeingInfo> convertResponseToList(String responseContent) throws IOException {
        // Remove markdown code blocks if present
        String cleanedContent = responseContent
            .replaceAll("```json\\n?", "")
            .replaceAll("```\\n?", "")
            .trim();

        return objectMapper.readValue(cleanedContent, 
            objectMapper.getTypeFactory().constructCollectionType(List.class, SightseeingInfo.class));
    }
}
