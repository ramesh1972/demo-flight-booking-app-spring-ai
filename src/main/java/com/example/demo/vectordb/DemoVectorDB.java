package com.example.demo.vectordb;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@Component
public class DemoVectorDB {

    private static DemoVectorDB instance;
    
    // In-memory collections
    private final Map<String, List<DocumentChunk>> indexes = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> indexMetadata = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // Configuration
    private static final int DEFAULT_CHUNK_SIZE = 500;
    private static final int CHUNK_OVERLAP = 50;

    public DemoVectorDB() {
        instance = this;
    }

    public static DemoVectorDB getInstance() {
        return instance;
    }

    @PostConstruct
    public void initialize() {
        System.out.println("🚀 Initializing DemoVectorDB singleton...");
        
        // Create some sample data
        createSampleData();
        
        // Load any existing data from workspace
        loadWorkspaceData();
        
        System.out.println("✅ DemoVectorDB initialized successfully with " + indexes.size() + " indexes");
        printIndexSummary();
    }

    /**
     * Create an index by processing files from a folder or single file
     */
    public void createIndex(String indexName, String dataPath) {
        System.out.println("📚 Creating index: " + indexName + " from: " + dataPath);
        
        List<DocumentChunk> chunks = new ArrayList<>();
        Set<String> processedFiles = new HashSet<>();
        
        try {
            Path path = Paths.get(dataPath);
            
            if (Files.isDirectory(path)) {
                // Process all files in directory
                try (Stream<Path> files = Files.walk(path)) {
                    files.filter(Files::isRegularFile)
                         .forEach(filePath -> {
                             processFile(filePath.toString(), chunks);
                             processedFiles.add(filePath.getFileName().toString());
                         });
                }
            } else if (Files.exists(path)) {
                // Process single file
                processFile(dataPath, chunks);
                processedFiles.add(path.getFileName().toString());
            } else {
                System.err.println("❌ Path does not exist: " + dataPath);
                return;
            }
            
            indexes.put(indexName, chunks);
            indexMetadata.put(indexName, processedFiles);
            
            System.out.println("✅ Index '" + indexName + "' created with " + chunks.size() + 
                             " chunks from " + processedFiles.size() + " files");
            
        } catch (IOException e) {
            System.err.println("❌ Error creating index: " + e.getMessage());
        }
    }

    /**
     * Search within a specific index
     */
    public SearchResult search(String indexName, String query) {
        long startTime = System.currentTimeMillis();
        
        List<DocumentChunk> results = new ArrayList<>();
        
        if (!indexes.containsKey(indexName)) {
            System.err.println("❌ Index not found: " + indexName);
            return new SearchResult(query, indexName, results, System.currentTimeMillis() - startTime);
        }
        
        List<DocumentChunk> indexChunks = indexes.get(indexName);
        String lowerQuery = query.toLowerCase();
        
        // Simple keyword-based search (simulating vector similarity)
        String[] queryWords = lowerQuery.split("\\s+");
        
        for (DocumentChunk chunk : indexChunks) {
            String lowerContent = chunk.getContent().toLowerCase();
            int score = 0;
            
            // Calculate relevance score
            for (String word : queryWords) {
                if (lowerContent.contains(word)) {
                    score += (word.length() > 3 ? 2 : 1); // Give more weight to longer words
                }
            }
            
            if (score > 0) {
                chunk.addMetadata("relevance_score", score);
                results.add(chunk);
            }
        }
        
        // Sort by relevance score (descending)
        results.sort((a, b) -> {
            Integer scoreA = (Integer) a.getMetadata().getOrDefault("relevance_score", 0);
            Integer scoreB = (Integer) b.getMetadata().getOrDefault("relevance_score", 0);
            return scoreB.compareTo(scoreA);
        });
        
        // Limit results
        if (results.size() > 10) {
            results = results.subList(0, 10);
        }
        
        long searchTime = System.currentTimeMillis() - startTime;
        
        System.out.println("🔍 Search in '" + indexName + "' for '" + query + 
                         "' found " + results.size() + " results in " + searchTime + "ms");
        
        return new SearchResult(query, indexName, results, searchTime);
    }

    /**
     * Get all available indexes
     */
    public Set<String> getIndexNames() {
        return new HashSet<>(indexes.keySet());
    }

    /**
     * Get index statistics
     */
    public Map<String, Object> getIndexStats(String indexName) {
        Map<String, Object> stats = new HashMap<>();
        
        if (!indexes.containsKey(indexName)) {
            stats.put("error", "Index not found");
            return stats;
        }
        
        List<DocumentChunk> chunks = indexes.get(indexName);
        Set<String> files = indexMetadata.get(indexName);
        
        stats.put("indexName", indexName);
        stats.put("totalChunks", chunks.size());
        stats.put("totalFiles", files != null ? files.size() : 0);
        stats.put("documentTypes", chunks.stream()
                .map(DocumentChunk::getDocumentType)
                .distinct()
                .toArray());
        
        return stats;
    }

    private void processFile(String filePath, List<DocumentChunk> chunks) {
        try {
            String content = Files.readString(Paths.get(filePath));
            String fileName = Paths.get(filePath).getFileName().toString();
            
            if (fileName.endsWith(".json")) {
                processJsonFile(content, fileName, chunks);
            } else {
                processTextFile(content, fileName, chunks);
            }
            
        } catch (IOException e) {
            System.err.println("❌ Error processing file " + filePath + ": " + e.getMessage());
        }
    }

    private void processJsonFile(String content, String fileName, List<DocumentChunk> chunks) {
        try {
            JsonNode jsonNode = objectMapper.readTree(content);
            
            if (jsonNode.isArray()) {
                // Handle JSON array
                for (int i = 0; i < jsonNode.size(); i++) {
                    String itemContent = objectMapper.writeValueAsString(jsonNode.get(i));
                    List<String> itemChunks = chunkText(itemContent, DEFAULT_CHUNK_SIZE);
                    
                    for (int j = 0; j < itemChunks.size(); j++) {
                        String chunkId = fileName + "_item_" + i + "_chunk_" + j;
                        DocumentChunk chunk = new DocumentChunk(chunkId, itemChunks.get(j), 
                                                              fileName, j, "json");
                        chunk.addMetadata("array_index", i);
                        chunks.add(chunk);
                    }
                }
            } else {
                // Handle single JSON object
                List<String> textChunks = chunkText(content, DEFAULT_CHUNK_SIZE);
                
                for (int i = 0; i < textChunks.size(); i++) {
                    String chunkId = fileName + "_chunk_" + i;
                    DocumentChunk chunk = new DocumentChunk(chunkId, textChunks.get(i), 
                                                          fileName, i, "json");
                    chunks.add(chunk);
                }
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error processing JSON file " + fileName + ": " + e.getMessage());
        }
    }

    private void processTextFile(String content, String fileName, List<DocumentChunk> chunks) {
        List<String> textChunks = chunkText(content, DEFAULT_CHUNK_SIZE);
        
        for (int i = 0; i < textChunks.size(); i++) {
            String chunkId = fileName + "_chunk_" + i;
            DocumentChunk chunk = new DocumentChunk(chunkId, textChunks.get(i), 
                                                  fileName, i, "document");
            chunks.add(chunk);
        }
    }

    private List<String> chunkText(String text, int chunkSize) {
        List<String> chunks = new ArrayList<>();
        
        if (text.length() <= chunkSize) {
            chunks.add(text);
            return chunks;
        }
        
        for (int i = 0; i < text.length(); i += chunkSize - CHUNK_OVERLAP) {
            int endIndex = Math.min(i + chunkSize, text.length());
            chunks.add(text.substring(i, endIndex));
            
            if (endIndex >= text.length()) {
                break;
            }
        }
        
        return chunks;
    }

    private void createSampleData() {
        System.out.println("📝 Creating sample data...");
        
        // Sample flight data
        List<DocumentChunk> flightChunks = new ArrayList<>();
        
        String[] sampleFlights = {
            "Flight AA123: New York to Los Angeles, departure 09:00, arrival 12:30, price $299",
            "Flight BA456: London to Paris, departure 14:15, arrival 16:45, price £189",
            "Flight LH789: Frankfurt to Tokyo, departure 22:30, arrival 17:20+1, price €899",
            "Flight QF001: Sydney to Melbourne, departure 06:45, arrival 08:15, price A$199"
        };
        
        for (int i = 0; i < sampleFlights.length; i++) {
            String chunkId = "sample_flight_" + i;
            DocumentChunk chunk = new DocumentChunk(chunkId, sampleFlights[i], 
                                                  "sample_flights.txt", i, "document");
            chunk.addMetadata("category", "flights");
            flightChunks.add(chunk);
        }
        
        indexes.put("sample_flights", flightChunks);
        indexMetadata.put("sample_flights", Set.of("sample_flights.txt"));
        
        // Sample JSON data
        List<DocumentChunk> jsonChunks = new ArrayList<>();
        
        String[] sampleJsons = {
            "{\"airline\":\"Emirates\",\"destination\":\"Dubai\",\"price\":599}",
            "{\"airline\":\"Singapore Airlines\",\"destination\":\"Singapore\",\"price\":449}",
            "{\"airline\":\"Lufthansa\",\"destination\":\"Munich\",\"price\":399}"
        };
        
        for (int i = 0; i < sampleJsons.length; i++) {
            String chunkId = "sample_json_" + i;
            DocumentChunk chunk = new DocumentChunk(chunkId, sampleJsons[i], 
                                                  "sample_airlines.json", i, "json");
            chunk.addMetadata("category", "airlines");
            jsonChunks.add(chunk);
        }
        
        indexes.put("sample_airlines", jsonChunks);
        indexMetadata.put("sample_airlines", Set.of("sample_airlines.json"));
    }

    private void loadWorkspaceData() {
        // Try to load flight_records_100.json from workspace root
        String workspaceFile = "flight_records_100.json";
        if (Files.exists(Paths.get(workspaceFile))) {
            createIndex("flight_records", workspaceFile);
        }
    }

    private void printIndexSummary() {
        System.out.println("\n📊 DemoVectorDB Index Summary:");
        System.out.println("================================");
        
        for (String indexName : indexes.keySet()) {
            Map<String, Object> stats = getIndexStats(indexName);
            System.out.println("🔸 Index: " + indexName);
            System.out.println("   - Chunks: " + stats.get("totalChunks"));
            System.out.println("   - Files: " + stats.get("totalFiles"));
            System.out.println("   - Types: " + Arrays.toString((Object[]) stats.get("documentTypes")));
        }
        
        System.out.println("================================\n");
    }
}