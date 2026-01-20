package com.example.demo.vectordb;

import java.util.*;

public class DocumentChunk {
    private String id;
    private String content;
    private String sourceFile;
    private int chunkIndex;
    private String documentType; // "json" or "document"
    private Map<String, Object> metadata;

    public DocumentChunk(String id, String content, String sourceFile, int chunkIndex, String documentType) {
        this.id = id;
        this.content = content;
        this.sourceFile = sourceFile;
        this.chunkIndex = chunkIndex;
        this.documentType = documentType;
        this.metadata = new HashMap<>();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    public int getChunkIndex() {
        return chunkIndex;
    }

    public void setChunkIndex(int chunkIndex) {
        this.chunkIndex = chunkIndex;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public void addMetadata(String key, Object value) {
        this.metadata.put(key, value);
    }

    @Override
    public String toString() {
        return "DocumentChunk{" +
                "id='" + id + '\'' +
                ", sourceFile='" + sourceFile + '\'' +
                ", chunkIndex=" + chunkIndex +
                ", documentType='" + documentType + '\'' +
                ", content='" + (content.length() > 100 ? content.substring(0, 100) + "..." : content) + '\'' +
                '}';
    }
}