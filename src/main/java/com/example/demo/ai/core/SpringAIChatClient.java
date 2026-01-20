package com.example.demo.ai.core;

import java.util.List;

import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

@Service
public class SpringAIChatClient {

    private final OpenAIClientFactory chatClientFactory;

    public SpringAIChatClient(OpenAIClientFactory chatClientFactory) {
        this.chatClientFactory = chatClientFactory;
    }

    public AzureOpenAiChatModel getChatModel() {
        return chatClientFactory.chatModel();
    }

    public String ask(String userInput) {
        return ChatClient.create(chatClientFactory.chatModel()).prompt()
                .user(userInput)
                .call()
                .content();
    }

    public String prompt(Prompt prompt) {
        List<Generation> generations = chatClientFactory.chatModel().call(prompt).getResults();
        return generations.get(0).toString();
    }
}
