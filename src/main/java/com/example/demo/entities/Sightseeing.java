package com.example.demo.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class Sightseeing {
    @JsonProperty("city")
    private String city;
    
    @JsonProperty("places")
    private List<String> places;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("bestTimeToVisit")
    private String bestTimeToVisit;
    
    @JsonProperty("averageCost")
    private Double averageCost;

    // Default constructor for Jackson
    public Sightseeing() {}

    // Constructor with all fields
    public Sightseeing(String city, List<String> places, String description, String bestTimeToVisit, Double averageCost) {
        this.city = city;
        this.places = places;
        this.description = description;
        this.bestTimeToVisit = bestTimeToVisit;
        this.averageCost = averageCost;
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

    public String getBestTimeToVisit() {
        return bestTimeToVisit;
    }

    public void setBestTimeToVisit(String bestTimeToVisit) {
        this.bestTimeToVisit = bestTimeToVisit;
    }

    public Double getAverageCost() {
        return averageCost;
    }

    public void setAverageCost(Double averageCost) {
        this.averageCost = averageCost;
    }

    @Override
    public String toString() {
        return "Sightseeing{" +
                "city='" + city + '\'' +
                ", places=" + places +
                ", description='" + description + '\'' +
                ", bestTimeToVisit='" + bestTimeToVisit + '\'' +
                ", averageCost=" + averageCost +
                '}';
    }
}
