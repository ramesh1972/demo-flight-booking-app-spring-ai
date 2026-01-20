package com.example.demo.controller;

import com.example.demo.vectordb.DemoVectorDB;
import com.example.demo.vectordb.SearchResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/vectordb")
@CrossOrigin(origins = "*")
public class VectorDBController {

    private final DemoVectorDB vectorDB;

    public VectorDBController(DemoVectorDB vectorDB) {
        this.vectorDB = vectorDB;
    }

    @PostMapping("/create-index")
    public ResponseEntity<Map<String, Object>> createIndex(
            @RequestParam String indexName,
            @RequestParam String dataPath) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            vectorDB.createIndex(indexName, dataPath);
            response.put("success", true);
            response.put("message", "Index '" + indexName + "' created successfully");
            response.put("indexName", indexName);
            response.put("dataPath", dataPath);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> search(
            @RequestParam String indexName,
            @RequestParam String query) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            SearchResult result = vectorDB.search(indexName, query);
            
            response.put("success", true);
            response.put("searchResult", result);
            response.put("contentSummary", result.getContentSummary());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/indexes")
    public ResponseEntity<Map<String, Object>> listIndexes() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Set<String> indexNames = vectorDB.getIndexNames();
            
            Map<String, Object> indexStats = new HashMap<>();
            for (String indexName : indexNames) {
                indexStats.put(indexName, vectorDB.getIndexStats(indexName));
            }
            
            response.put("success", true);
            response.put("indexes", indexNames);
            response.put("indexStats", indexStats);
            response.put("totalIndexes", indexNames.size());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/index/{indexName}/stats")
    public ResponseEntity<Map<String, Object>> getIndexStats(@PathVariable String indexName) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Map<String, Object> stats = vectorDB.getIndexStats(indexName);
            
            if (stats.containsKey("error")) {
                response.put("success", false);
                response.put("error", stats.get("error"));
            } else {
                response.put("success", true);
                response.put("stats", stats);
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/demo-search")
    public ResponseEntity<Map<String, Object>> demoSearch(
            @RequestParam(defaultValue = "sample_flights") String indexName,
            @RequestParam(defaultValue = "New York") String query) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            SearchResult result = vectorDB.search(indexName, query);
            
            response.put("success", true);
            response.put("message", "Demo search completed");
            response.put("searchResult", result);
            response.put("availableIndexes", vectorDB.getIndexNames());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Set<String> indexNames = vectorDB.getIndexNames();
            
            response.put("success", true);
            response.put("status", "VectorDB is running");
            response.put("totalIndexes", indexNames.size());
            response.put("availableIndexes", indexNames);
            response.put("instance", DemoVectorDB.getInstance() != null ? "Active" : "Inactive");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
}