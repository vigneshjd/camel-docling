package com.example.docling.rag.config;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.output.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Configuration factory for LangChain4j chat models.
 * This class creates and configures the chat model used for RAG queries.
 */
public class ChatModelFactory {
    
    private static final Logger LOG = LoggerFactory.getLogger(ChatModelFactory.class);
    
    /**
     * Create a ChatLanguageModel based on environment configuration.
     * Falls back to a mock model if OpenAI API key is not configured.
     */
    public static ChatLanguageModel createChatModel() {
        String apiKey = System.getenv("OPENAI_API_KEY");
        
        if (apiKey != null && !apiKey.isEmpty()) {
            LOG.info("Configuring OpenAI chat model");
            return OpenAiChatModel.builder()
                    .apiKey(apiKey)
                    .modelName("gpt-3.5-turbo")
                    .temperature(0.7)
                    .maxTokens(500)
                    .build();
        } else {
            LOG.warn("OPENAI_API_KEY not found. Using mock chat model.");
            LOG.warn("To use OpenAI, set the OPENAI_API_KEY environment variable.");
            return createMockChatModel();
        }
    }
    
    /**
     * Create a mock chat model for testing without API keys.
     */
    private static ChatLanguageModel createMockChatModel() {
        return new ChatLanguageModel() {
            @Override
            public Response<AiMessage> generate(List<ChatMessage> messages) {
                String userMessage = messages.isEmpty() ? "" : 
                    messages.get(messages.size() - 1).toString();
                LOG.info("Mock chat model received message: {}", userMessage);
                String responseText = "This is a mock response. The actual query was processed, but no OpenAI API key is configured. " +
                       "To get real AI responses, please set the OPENAI_API_KEY environment variable with your OpenAI API key. " +
                       "\n\nYour query and the retrieved context have been processed successfully by the RAG system.";
                return Response.from(AiMessage.from(responseText));
            }
        };
    }
}
