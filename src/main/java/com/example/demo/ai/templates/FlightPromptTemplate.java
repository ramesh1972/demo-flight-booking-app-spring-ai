package com.example.demo.ai.templates;

import com.example.demo.ai.converters.FlightStructuredOutputConverter;
import com.example.demo.ai.converters.BookedFlightStructuredOutputConverter;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FlightPromptTemplate {

    private final FlightStructuredOutputConverter flightConverter;
    private final BookedFlightStructuredOutputConverter bookedFlightConverter;
   public FlightPromptTemplate(
            FlightStructuredOutputConverter flightConverter,
            BookedFlightStructuredOutputConverter bookedFlightConverter) {
        this.flightConverter = flightConverter;
        this.bookedFlightConverter = bookedFlightConverter;
    }
    public Prompt createFlightSearchPrompt(String from, String to, String date) {
        return createFlightSearchPrompt(from, to, date, "", "");
    }

    public Prompt createFlightSearchPrompt(String from, String to, String date, String flightContext, String sightseeingContext) {
        // Handle null parameters
        String fromValue = (from != null && !from.trim().isEmpty()) ? from : "any city";
        String toValue = (to != null && !to.trim().isEmpty()) ? to : "any city";
        String dateValue = (date != null && !date.trim().isEmpty()) ? date : "any date";
        
        String template = """
            Search for flights from {from} to {to} on {date}.
            
            Available flight data from our database:
            {flightContext}
            
            Sightseeing information for {to}:
            {sightseeingContext}
            
            Generate a realistic flight option with appropriate airline, flight number, 
            departure and arrival times, and pricing based on the available data above.
            If available data matches the search criteria, use it. Otherwise, generate realistic data.
            {format}
            """;
            
        return new Prompt(
            PromptTemplate.builder()
                .template(template)
                .variables(Map.of(
                    "from", fromValue,
                    "to", toValue, 
                    "date", dateValue,
                    "flightContext", (flightContext != null && !flightContext.isEmpty()) ? flightContext : "No matching flights found in database.",
                    "sightseeingContext", (sightseeingContext != null && !sightseeingContext.isEmpty()) ? sightseeingContext : "No sightseeing information available.",
                    "format", flightConverter.getFormat()
                ))
                .build().createMessage()
        );
    }

    public Prompt createFlightSearchPromptForList(String from, String to, String date, String flightContext, String sightseeingContext) {
        // Handle null parameters
        String fromValue = (from != null && !from.trim().isEmpty()) ? from : "any city";
        String toValue = (to != null && !to.trim().isEmpty()) ? to : "any city";
        String dateValue = (date != null && !date.trim().isEmpty()) ? date : "any date";
        
        String template = """
            Search for ALL flights from {from} to {to} on {date}.
            
            Available flight data from our database:
            {flightContext}
            
            Sightseeing information for {to}:
            {sightseeingContext}
            
            Return ALL matching flights from the database as a JSON array. Each flight should have:
            - airline: airline name
            - flightNo: flight number
            - departureCity: departure city
            - destinationCity: destination city  
            - departureDate: departure date in format yyyy-MM-dd'T'HH:mm:ss
            - arrivalDate: arrival date in format yyyy-MM-dd'T'HH:mm:ss
            - price: price in number format
            
            IMPORTANT: Return a JSON array of ALL matching flights, not just one flight.
            If multiple flights match the criteria, return all of them.
            Use the exact data from the database above.
            
            Example format:
            [
              {{
                "airline": "IndiGo",
                "flightNo": "6E101",
                "departureCity": "Delhi",
                "destinationCity": "Mumbai",
                "departureDate": "2025-06-15T08:00:00",
                "arrivalDate": "2025-06-15T10:15:00",
                "price": 4500
              }},
              {{
                "airline": "SpiceJet",
                "flightNo": "SG020",
                "departureCity": "Goa",
                "destinationCity": "Mumbai",
                "departureDate": "2025-07-10T19:30:00",
                "arrivalDate": "2025-07-10T20:45:00",
                "price": 3400
              }}
            ]
            """;
            
        return new Prompt(
            PromptTemplate.builder()
                .template(template)
                .variables(Map.of(
                    "from", fromValue,
                    "to", toValue, 
                    "date", dateValue,
                    "flightContext", (flightContext != null && !flightContext.isEmpty()) ? flightContext : "No matching flights found in database.",
                    "sightseeingContext", (sightseeingContext != null && !sightseeingContext.isEmpty()) ? sightseeingContext : "No sightseeing information available."
                ))
                .build().createMessage()
        );
    }

    public Prompt createFlightBookingPrompt(String outboundFlightNo, String returnFlightNo, String passengerInfo) {
        return createFlightBookingPrompt(outboundFlightNo, returnFlightNo, passengerInfo, "");
    }

    public Prompt createFlightBookingPrompt(String outboundFlightNo, String returnFlightNo, String passengerInfo, String flightContext) {
        String template = """
            Create a flight booking for passenger: {passengerInfo}
            Outbound flight number: {outboundFlightNo}
            Return flight number: {returnFlightNo}
            
            Available flight data from our database:
            {flightContext}
            
            Generate complete flight details for both outbound and return flights including
            realistic airline information, departure/arrival cities, dates, and pricing.
            Use the available data above if it matches the flight numbers. Otherwise, generate realistic data.
            {format}
            """;
            
        return new Prompt(
            PromptTemplate.builder()
                .template(template)
                .variables(Map.of(
                    "outboundFlightNo", outboundFlightNo,
                    "returnFlightNo", returnFlightNo,
                    "passengerInfo", passengerInfo,
                    "flightContext", (flightContext != null && !flightContext.isEmpty()) ? flightContext : "No matching flights found in database.",
                    "format", bookedFlightConverter.getFormat()
                ))
                .build().createMessage()
        );
    }

    // Getter methods for converters (useful for direct access)
    public FlightStructuredOutputConverter getFlightConverter() {
        return flightConverter;
    }

    public BookedFlightStructuredOutputConverter getBookedFlightConverter() {
        return bookedFlightConverter;
    }
}