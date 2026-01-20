package com.example.demo.ai.core;

import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.azure.openai.AzureOpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class OpenAIClientFactory {

    @Autowired
    private OpenAIConfig openAIConfig;

    @Bean
    public AzureOpenAiChatModel chatModel() {

        OpenAIClientBuilder openAIClientBuilder = createClientBuilder();

        AzureOpenAiChatOptions options = AzureOpenAiChatOptions.builder()
                // In Azure, this should be the *deployment name* from Azure AI Studio
                .deploymentName(openAIConfig.getModel()) 
                .temperature(openAIConfig.getTemperature())
                .maxTokens(openAIConfig.getMaxTokens())
                .topP(openAIConfig.getTopP())
                .build();

        return AzureOpenAiChatModel.builder()
                .openAIClientBuilder(openAIClientBuilder)
                .defaultOptions(options)
                .build();
    }

    public OpenAIClientBuilder createClientBuilder() {
        return new OpenAIClientBuilder()
                .endpoint(openAIConfig.getEndpoint())
                .credential(new AzureKeyCredential(openAIConfig.getApiKey()));               
    }
}