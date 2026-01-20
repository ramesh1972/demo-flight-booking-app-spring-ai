package com.example.demo.ai.core;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAIConfig {

    @Value("${spring.ai.azure.openai.endpoint}")
    private String endpoint;

    @Value("${spring.ai.azure.openai.api-key}")
    private String apiKey;

    @Value("${spring.ai.azure.openai.model}")
    private String model;

    @Value("${spring.ai.azure.openai.chat.options.deployment-name}")
    private String deploymentId;

    @Value("${spring.ai.azure.openai.api-version}")
    private String apiVersion;

    @Value("${spring.ai.azure.openai.chat.options.max-tokens}")
    private Integer maxTokens;

    @Value("${spring.ai.azure.openai.chat.options.temperature}")
    private Double temperature;

    @Value("${spring.ai.azure.openai.chat.options.topP}")
    private Double topP;

    public String getEndpoint() {
        return endpoint;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getModel() {
        return model;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public Integer getMaxTokens() {
        return maxTokens != null ? maxTokens : 4096;
    }

    public Double getTemperature() {
        return temperature != null ? temperature : 0.0;
    }

    public Double getTopP() {
        return topP != null ? topP : 0.0;
    }

}