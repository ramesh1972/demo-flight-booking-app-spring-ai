package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.service.ChatService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    private final ChatService chatService;
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/message")
    public ResponseEntity<Map<String, Object>> sendMessage(@RequestParam String prompt) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Send message to AI and get response
            String aiResponse = chatService.sendMessage(prompt);
            
            response.put("success", true);
            response.put("message", "Message processed successfully");
            response.put("response", aiResponse);
            response.put("prompt", prompt);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("message", "Failed to process message - check your AI configuration");
        }
        
        return ResponseEntity.ok(response);
    }
}