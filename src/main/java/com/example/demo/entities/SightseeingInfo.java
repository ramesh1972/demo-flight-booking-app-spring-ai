package com.example.demo.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SightseeingInfo {
    @JsonProperty("city")
    private String city;
    
    @JsonProperty("places")
    private List<String> places;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("outgoingFlights")
    private List<Flight> outgoingFlights;

    // Default constructor
    public SightseeingInfo() {}

    // Constructor
    public SightseeingInfo(String city, List<String> places, String description, List<Flight> outgoingFlights) {
        this.city = city;
        this.places = places;
        this.description = description;
        this.outgoingFlights = outgoingFlights;
    }

    // Getters and Setters
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<String> getPlaces() {
        return places;
    }

    public void setPlaces(List<String> places) {
        this.places = places;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Flight> getOutgoingFlights() {
        return outgoingFlights;
    }

    public void setOutgoingFlights(List<Flight> outgoingFlights) {
        this.outgoingFlights = outgoingFlights;
    }
}
