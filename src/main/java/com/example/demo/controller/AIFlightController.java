package com.example.demo.controller;

import com.example.demo.entities.Flight;
import com.example.demo.service.FlightAIService;
import com.example.demo.entities.BookedFlight;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/ai/flights")
@CrossOrigin(origins = "*")
public class AIFlightController {

    private final FlightAIService flightAIService;

    public AIFlightController(FlightAIService flightAIService) {
        this.flightAIService = flightAIService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<Flight>> searchFlight(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) String date) {
        try {
            List<Flight> flights = flightAIService.searchFlight(from, to, date);
            return ResponseEntity.ok(flights);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/book")
    public ResponseEntity<BookedFlight> bookFlight(
            @RequestParam String outboundFlightNo,
            @RequestParam(required = false) String returnFlightNo,
            @RequestParam String passengerInfo) {
        try {
            BookedFlight bookedFlight = flightAIService.bookFlight(outboundFlightNo, returnFlightNo, passengerInfo);
            return ResponseEntity.ok(bookedFlight);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/booked-flights")
    public ResponseEntity<List<BookedFlight>> getBookedFlights() {
        try {
            List<BookedFlight> bookedFlights = flightAIService.getAllBookedFlights();
            return ResponseEntity.ok(bookedFlights);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }

    }
}