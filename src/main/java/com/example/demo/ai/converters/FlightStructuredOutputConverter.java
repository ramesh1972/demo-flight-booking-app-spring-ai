package com.example.demo.ai.converters;

import com.example.demo.entities.Flight;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.ai.converter.StructuredOutputConverter;
import org.springframework.stereotype.Component;

@Component
public class FlightStructuredOutputConverter implements StructuredOutputConverter<Flight> {

    private final ObjectMapper objectMapper;

    public FlightStructuredOutputConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Flight convert(String source) {
        try {
            return objectMapper.readValue(source, Flight.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert JSON to Flight object", e);
        }
    }

    @Override
    public String getFormat() {
        return """
            Your response should be in JSON format.
            The data structure for the JSON should match this Java class: com.example.demo.entities.Flight
            Include the following fields:
            - airline (String): The airline name
            - flightNo (String): The flight number
            - departureCity (String): The departure city
            - destinationCity (String): The destination city
            - departureDate (String): The departure date in ISO-8601 format (e.g., "2025-01-20T10:30:00")
            - arrivalDate (String): The arrival date in ISO-8601 format (e.g., "2025-01-20T14:30:00")
            - price (double): The flight price as a decimal number
            Do not include any explanations, only provide a RFC8259 compliant JSON response following this format without deviation.
            """;
    }
}