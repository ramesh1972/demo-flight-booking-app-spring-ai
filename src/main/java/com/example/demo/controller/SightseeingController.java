package com.example.demo.controller;

import com.example.demo.entities.Sightseeing;
import com.example.demo.entities.SightseeingInfo;
import com.example.demo.service.SightseeingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sightseeing")
@CrossOrigin(origins = "*")
public class SightseeingController {

    private final SightseeingService sightseeingService;

    public SightseeingController(SightseeingService sightseeingService) {
        this.sightseeingService = sightseeingService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<Sightseeing>> searchSightseeing(
            @RequestParam(required = true) String prompt) {
        try {
            List<Sightseeing> sightseeingList = sightseeingService.searchSightseeing(prompt);
            return ResponseEntity.ok(sightseeingList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/search-with-flights")
    public ResponseEntity<List<SightseeingInfo>> searchWithFlights(
            @RequestParam(required = true) String prompt) {
        try {
            List<SightseeingInfo> results = sightseeingService.searchWithFlights(prompt);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
