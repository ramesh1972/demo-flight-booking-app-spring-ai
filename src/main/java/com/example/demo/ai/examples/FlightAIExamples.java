package com.example.demo.ai.examples;

import com.example.demo.entities.Flight;
import com.example.demo.service.FlightAIService;
import com.example.demo.entities.BookedFlight;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Example usage of the Flight AI Service with structured output
 * This class demonstrates how to use the Spring AI Prompt Templates
 * with structured output converters for flight-related entities.
 */
@Component
public class FlightAIExamples implements CommandLineRunner {

    private final FlightAIService flightAIService;

    public FlightAIExamples(FlightAIService flightAIService) {
        this.flightAIService = flightAIService;
    }

    @Override
    public void run(String... args) throws Exception {
        // Uncomment these examples to test the AI service
        // runFlightSearchExample();
        // runBookingExample();
        // runPaymentExample();
    }

    public void runFlightSearchExample() {
        try {
            System.out.println("=== Flight Search Example ===");
            java.util.List<Flight> flights = flightAIService.searchFlight("New York", "Los Angeles", "2025-02-15");
            System.out.println("Generated Flights: " + flights);
        } catch (Exception e) {
            System.err.println("Error in flight search: " + e.getMessage());
        }
    }

    public void runBookingExample() {
        try {
            System.out.println("=== Flight Booking Example ===");
            BookedFlight booking = flightAIService.bookFlight(
                "AA123", "AA456", "John Doe, john@example.com");
            System.out.println("Generated Booking: " + booking);
        } catch (Exception e) {
            System.err.println("Error in flight booking: " + e.getMessage());
        }
    }
}