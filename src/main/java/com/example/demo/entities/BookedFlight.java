package com.example.demo.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BookedFlight {

    @JsonProperty("flight")
    private Flight flight = null;
    
    @JsonProperty("returnFlight")
    private Flight returnFlight = null;

    // Default constructor for Jackson
    public BookedFlight() {}

    public BookedFlight(String flightNumber, String returnFlightNumber) {
        this.flight = new Flight();
        this.flight.setFlightNo(flightNumber);
        this.returnFlight = new Flight();
        this.returnFlight.setFlightNo(returnFlightNumber);
    }

    // Constructor with Flight objects
    public BookedFlight(Flight flight, Flight returnFlight) {
        this.flight = flight;
        this.returnFlight = returnFlight;
    }

    // Getters and Setters
    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public Flight getReturnFlight() {
        return returnFlight;
    }

    public void setReturnFlight(Flight returnFlight) {
        this.returnFlight = returnFlight;
    }

    @Override
    public String toString() {
        return "BookedFlight{" +
                "flight=" + flight +
                ", returnFlight=" + returnFlight +
                '}';
    }
}
