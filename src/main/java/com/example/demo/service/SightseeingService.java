package com.example.demo.service;

import com.example.demo.ai.converters.SightseeingStructuredOutputConverter;
import com.example.demo.ai.converters.SightseeingWithFlightsConverter;
import com.example.demo.ai.templates.SightseeingPromptTemplate;
import com.example.demo.entities.Flight;
import com.example.demo.entities.Sightseeing;
import com.example.demo.entities.SightseeingInfo;
import com.example.demo.vectordb.DemoVectorDB;
import com.example.demo.vectordb.SearchResult;
import com.example.demo.vectordb.DocumentChunk;

import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SightseeingService {

    private final AzureOpenAiChatModel chatModel;
    private final SightseeingPromptTemplate promptTemplate;
    private final DemoVectorDB vectorDB;
    private final SightseeingStructuredOutputConverter converter;
    private final SightseeingWithFlightsConverter withFlightsConverter;
    private final FlightAIService flightAIService;
    private final ChatService chatService;

    public SightseeingService(AzureOpenAiChatModel chatModel, SightseeingPromptTemplate promptTemplate,
            DemoVectorDB vectorDB, SightseeingStructuredOutputConverter converter,
            SightseeingWithFlightsConverter withFlightsConverter, FlightAIService flightAIService,
            ChatService chatService) {
        this.chatModel = chatModel;
        this.promptTemplate = promptTemplate;
        this.vectorDB = vectorDB;
        this.converter = converter;
        this.withFlightsConverter = withFlightsConverter;
        this.flightAIService = flightAIService;
        this.chatService = chatService;
    }

    public List<Sightseeing> searchSightseeing(String prompt) {
        // Search vectorDB for sightseeing information
        SearchResult sightseeingResults = vectorDB.search("sightseeing", prompt);

        String sightseeingContext = sightseeingResults.getResults().stream()
                .limit(20)
                .map(DocumentChunk::getContent)
                .collect(Collectors.joining("\n"));

        System.out.println("Sightseeing Context:\n" + sightseeingContext);

        // Create prompt for LLM
        Prompt llmPrompt = promptTemplate.createSightseeingSearchPrompt(prompt, sightseeingContext);

        // Call LLM and get response
        String response = ChatClient.create(chatModel).prompt(llmPrompt).call().content();

        System.out.println("LLM Response:\n" + response);

        // Parse the response
        try {
            Sightseeing sightseeing = converter.convertResponse(response);
            List<Sightseeing> result = new ArrayList<>();
            result.add(sightseeing);
            return result;
        } catch (Exception e) {
            System.err.println("Error parsing LLM response: " + e.getMessage());
            System.err.println("Response was: " + response);
            e.printStackTrace();

            // Return empty list on error
            return new ArrayList<>();
        }
    }

    public List<SightseeingInfo> searchWithFlights(String prompt) {
        // STEP 1: Get sightseeing information
        System.out.println("STEP 1: Searching sightseeing information for: " + prompt);
        SearchResult sightseeingResults = vectorDB.search("sightseeing", prompt);

        String sightseeingContext = sightseeingResults.getResults().stream()
                .limit(20)
                .map(DocumentChunk::getContent)
                .collect(Collectors.joining("\n"));

        System.out.println("Found " + sightseeingResults.getResults().size() + " sightseeing records");
        System.out.println("Sightseeing Context:\n" + sightseeingContext);

        // STEP 2: Extract cities using LLM via chat service
        System.out.println("\nSTEP 2: Extracting city information using LLM");
        String cities = "";
        
        if (!sightseeingContext.isEmpty()) {
            String cityExtractionPrompt = "From this context: \"" + sightseeingContext
                    + "\", extract ONLY the city names mentioned as a comma-separated list. "
                    + "If no city is found, return an empty string. "
                    + "Example: If context mentions Mumbai and Delhi, return 'Mumbai,Delhi'";
            cities = chatService.sendMessage(cityExtractionPrompt).trim();
            System.out.println("Extracted cities: " + cities);
        } else {
            System.out.println("No sightseeing context found, skipping city extraction.");
        }

        // STEP 3: Get outgoing flights for each extracted city
        System.out.println("\nSTEP 3: Getting outgoing flights from extracted cities");
        List<Flight> outgoingFlights = new ArrayList<>();
        
        if (!cities.isEmpty()) {
            try {
                for (String city : cities.split(",")) {
                    String trimmedCity = city.trim();
                    if (trimmedCity.isEmpty()) {
                        continue;
                    }
                    System.out.println("Searching flights from: " + trimmedCity);
                    List<Flight> flightsFromCity = flightAIService.searchFlight(null, trimmedCity, null);
                    outgoingFlights.addAll(flightsFromCity);
                    System.out.println("Found " + flightsFromCity.size() + " outgoing flights from " + trimmedCity);
                }
                System.out.println("Total outgoing flights: " + outgoingFlights.size());
            } catch (Exception e) {
                System.err.println("Error getting flights: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // Convert flights to context string
        String flightContext = outgoingFlights.stream()
                .map(flight -> String.format("Flight %s: %s -> %s, Departure: %s, Arrival: %s, Price: %.2f",
                        flight.getFlightNo(), flight.getDepartureCity(),
                        flight.getDestinationCity(), flight.getDepartureDate(),
                        flight.getArrivalDate(), flight.getPrice()))
                .collect(Collectors.joining("\n"));

        System.out.println("\nFlight Context:\n" + flightContext);

        // STEP 4: Pass combined data to LLM to generate structured SightseeingInfo
        System.out.println("\nSTEP 4: Sending to LLM for structured response");
        String template = """
            Based on the sightseeing information:
            {sightseeingContext}
            
            And the available outgoing flights from these cities:
            {flightContext}
            
            Please provide a structured response as a JSON ARRAY of SightseeingInfo objects.
            For EACH city mentioned in the sightseeing context, create a separate object containing:
            1. The city name
            2. A list of tourist places/attractions for that city
            3. A brief description of that destination
            4. The list of outgoing flights FROM that city (match departureCity with city name)
            
            {format}
            
            IMPORTANT: Return ONLY the JSON ARRAY without any markdown formatting or additional text.
            """;

        Prompt llmPrompt = new Prompt(
                PromptTemplate.builder()
                        .template(template)
                        .variables(Map.of(
                                "sightseeingContext", sightseeingContext.isEmpty() ? "No sightseeing information found." : sightseeingContext,
                                "flightContext", flightContext.isEmpty() ? "No outgoing flights found." : flightContext,
                                "format", withFlightsConverter.getFormat()
                        ))
                        .build().createMessage()
        );

        // Call LLM and get response
        String response = ChatClient.create(chatModel).prompt(llmPrompt).call().content();

        System.out.println("\nLLM Response:\n" + response);

        // Parse the response
        try {
            List<SightseeingInfo> result = withFlightsConverter.convertResponseToList(response);
            System.out.println("Successfully created " + result.size() + " SightseeingInfo objects");
            result.forEach(info -> System.out.println("  - " + info.getCity()));
            return result;
        } catch (Exception e) {
            System.err.println("Error parsing LLM response: " + e.getMessage());
            System.err.println("Response was: " + response);
            e.printStackTrace();

            // Return empty list on error
            return new ArrayList<>();
        }
    }
}
