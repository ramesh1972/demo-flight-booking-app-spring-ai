package com.example.demo.vectordb;

import java.util.*;
import java.util.stream.Collectors;

public class SearchResult {
    private List<DocumentChunk> results;
    private String query;
    private String indexName;
    private long searchTimeMs;
    private int totalResults;

    public SearchResult(String query, String indexName, List<DocumentChunk> results, long searchTimeMs) {
        this.query = query;
        this.indexName = indexName;
        this.results = results != null ? results : new ArrayList<>();
        this.totalResults = this.results.size();
        this.searchTimeMs = searchTimeMs;
    }

    // Getters and Setters
    public List<DocumentChunk> getResults() {
        return results;
    }

    public void setResults(List<DocumentChunk> results) {
        this.results = results;
        this.totalResults = results != null ? results.size() : 0;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public long getSearchTimeMs() {
        return searchTimeMs;
    }

    public void setSearchTimeMs(long searchTimeMs) {
        this.searchTimeMs = searchTimeMs;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public List<String> getContentSummary() {
        return results.stream()
                .map(chunk -> chunk.getSourceFile() + " (chunk " + chunk.getChunkIndex() + "): " + 
                             (chunk.getContent().length() > 150 ? 
                              chunk.getContent().substring(0, 150) + "..." : 
                              chunk.getContent()))
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "SearchResult{" +
                "query='" + query + '\'' +
                ", indexName='" + indexName + '\'' +
                ", totalResults=" + totalResults +
                ", searchTimeMs=" + searchTimeMs +
                '}';
    }
}