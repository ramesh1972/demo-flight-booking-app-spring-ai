package com.example.demo.service;

import com.example.demo.ai.core.SpringAIChatClient;
import com.example.demo.ai.templates.FlightPromptTemplate;
import com.example.demo.entities.Flight;
import com.example.demo.entities.BookedFlight;
import com.example.demo.vectordb.DemoVectorDB;
import com.example.demo.vectordb.SearchResult;
import com.example.demo.vectordb.DocumentChunk;

import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FlightAIService {

    private final SpringAIChatClient chatClient;
    private final FlightPromptTemplate promptService;
    private final DemoVectorDB vectorDB;
    private final List<BookedFlight> bookedFlights = new ArrayList<>();

    public FlightAIService(SpringAIChatClient chatClient, FlightPromptTemplate promptService, DemoVectorDB vectorDB) {
        this.chatClient = chatClient;
        this.promptService = promptService;
        this.vectorDB = vectorDB;
    }

    public List<Flight> searchFlight(String from, String to, String date) {
        // RAG look up flights in dummy vectorDB
        // Build dynamic search query based on provided parameters
        StringBuilder searchQuery = new StringBuilder();

        if (from != null && !from.trim().isEmpty()) {
            searchQuery.append(from).append(" ");
        }
        if (to != null && !to.trim().isEmpty()) {
            searchQuery.append(to).append(" ");
        }
        if (date != null && !date.trim().isEmpty()) {
            searchQuery.append(date).append(" ");
        }

        // If no parameters provided, search for all flights
        String query = searchQuery.toString().trim();
        if (query.isEmpty()) {
            query = "flight";
        }

        SearchResult flightResults = vectorDB.search("flights", query);

        // Extract relevant flight data
        String flightContext = flightResults.getResults().stream()
                .limit(20)
                .map(DocumentChunk::getContent)
                .collect(Collectors.joining("\n"));

        System.out.println("Flight Search Context:\n" + flightContext);

        // Create prompt with context
        String fromParam = (from != null && !from.trim().isEmpty()) ? from : "any city";
        String toParam = (to != null && !to.trim().isEmpty()) ? to : "any city";
        String dateParam = (date != null && !date.trim().isEmpty()) ? date : "any date";

        Prompt prompt = promptService.createFlightSearchPromptForList(fromParam, toParam, dateParam, flightContext, "");
        AzureOpenAiChatModel model = chatClient.getChatModel();

        return ChatClient.create(model).prompt(prompt).call().entity(new ParameterizedTypeReference<List<Flight>>() {
        });
    }

    public BookedFlight bookFlight(String outboundFlightNo, String returnFlightNo, String passengerInfo) {
        // Create booked flight object
        BookedFlight bookedFlight = new BookedFlight(outboundFlightNo, returnFlightNo != null ? returnFlightNo : "");

        // Store in memory
        bookedFlights.add(bookedFlight);

        System.out.println("✅ Flight booked! Total bookings: " + bookedFlights.size());
        System.out.println("   Outbound: " + outboundFlightNo);
        System.out.println("   Return: " + (returnFlightNo != null ? returnFlightNo : "None"));
        System.out.println("   Passenger: " + passengerInfo);

        return bookedFlight;
    }

    public List<BookedFlight> getAllBookedFlights() {
        return new ArrayList<>(bookedFlights);
    }
}