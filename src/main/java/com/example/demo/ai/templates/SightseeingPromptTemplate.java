package com.example.demo.ai.templates;

import com.example.demo.ai.converters.SightseeingStructuredOutputConverter;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SightseeingPromptTemplate {

    private final SightseeingStructuredOutputConverter sightseeingConverter;

    public SightseeingPromptTemplate(SightseeingStructuredOutputConverter sightseeingConverter) {
        this.sightseeingConverter = sightseeingConverter;
    }

    public Prompt createSightseeingSearchPrompt(String query, String sightseeingContext) {
        String template = """
            Based on the user's query: "{query}"
            
            Here is information from our sightseeing database:
            {sightseeingContext}
            
            Please provide information about the destination including:
            1. The city name
            2. List of tourist attractions and places to visit
            3. A brief description of the destination and its attractions
            4. Best time to visit (season/months)
            5. Average estimated cost per person for a day visit
            
            {format}
            
            IMPORTANT: Return ONLY the JSON object without any markdown formatting or additional text.
            """;

        return new Prompt(
            PromptTemplate.builder()
                .template(template)
                .variables(Map.of(
                    "query", query != null ? query : "",
                    "sightseeingContext", sightseeingContext.isEmpty() ? "No sightseeing information found." : sightseeingContext,
                    "format", sightseeingConverter.getFormat()
                ))
                .build().createMessage()
        );
    }
}
