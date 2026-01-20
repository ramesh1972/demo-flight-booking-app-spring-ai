package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.ai.core.SpringAIChatClient;

@Service
public class ChatService {

    @Autowired
    //private OpenAIClientFactory openAIClientFactory;
    private final SpringAIChatClient springAIChatClient;

    public ChatService(SpringAIChatClient springAIChatClient) {
        this.springAIChatClient = springAIChatClient;
    }
    public String sendMessage(String message) {
        return springAIChatClient.ask(message);
    }
} 
