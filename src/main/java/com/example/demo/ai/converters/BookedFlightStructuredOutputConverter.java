package com.example.demo.ai.converters;

import com.example.demo.entities.BookedFlight;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.ai.converter.StructuredOutputConverter;
import org.springframework.stereotype.Component;

@Component
public class BookedFlightStructuredOutputConverter implements StructuredOutputConverter<BookedFlight> {

    private final ObjectMapper objectMapper;

    public BookedFlightStructuredOutputConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public BookedFlight convert(String source) {
        try {
            return objectMapper.readValue(source, BookedFlight.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert JSON to BookedFlight object", e);
        }
    }

    @Override
    public String getFormat() {
        return """
            Your response should be in JSON format.
            The data structure for the JSON should match this Java class: com.example.demo.entities.BookedFlight
            Include the following fields:
            - flight (Flight object): The main flight with all Flight properties (airline, flightNo, departureCity, destinationCity, departureDate, arrivalDate, price)
            - returnFlight (Flight object): The return flight with all Flight properties (airline, flightNo, departureCity, destinationCity, departureDate, arrivalDate, price)
            Use ISO-8601 format for dates (e.g., "2025-01-20T10:30:00").
            Do not include any explanations, only provide a RFC8259 compliant JSON response following this format without deviation.
            """;
    }
}