package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.example.demo.service.ChatService;
import com.example.demo.vectordb.DemoVectorDB;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.demo")
public class DemoApplication {

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		return mapper;
	}

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(DemoApplication.class, args);

		// Initialize VectorDB
		DemoVectorDB vectorDB = context.getBean(DemoVectorDB.class);
		System.out.println("🔍 VectorDB initialized with " + vectorDB.getIndexNames().size() + " indexes");

		// Index flights data from data/jsons folder
		String flightsJsonPath = "src/main/java/com/example/demo/data/jsons/flights.json";
		vectorDB.createIndex("flights", flightsJsonPath);
		
		// Index sightseeing data from data/docs folder
		String sightseeingDocsPath = "src/main/java/com/example/demo/data/docs";
		vectorDB.createIndex("sightseeing", sightseeingDocsPath);

		// Get the service from the application context
		ChatService chatService = context.getBean(ChatService.class);
		
		// test message sending
		String response = chatService.sendMessage("Can you help me?");
		System.out.println("AI Response: " + response);
	}
}