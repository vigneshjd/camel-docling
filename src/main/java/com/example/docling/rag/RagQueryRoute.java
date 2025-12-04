package com.example.docling.rag;

import com.example.docling.rag.service.VectorStoreService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Camel route for handling RAG queries using LangChain4j.
 * This route receives user queries, retrieves relevant document chunks from the vector store,
 * and uses LangChain4j to generate AI-powered responses.
 */
public class RagQueryRoute extends RouteBuilder {
    
    private static final Logger LOG = LoggerFactory.getLogger(RagQueryRoute.class);
    private static final int MAX_RELEVANT_CHUNKS = 5;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public void configure() throws Exception {
        
        // Get VectorStoreService from registry (registered in DocumentIngestionRoute)
        VectorStoreService vectorStoreService = getContext().getRegistry()
                .lookupByNameAndType("vectorStoreService", VectorStoreService.class);
        
        if (vectorStoreService == null) {
            throw new IllegalStateException("VectorStoreService not found in registry");
        }
        
        // Route 1: HTTP endpoint for RAG queries
        from("jetty:http://0.0.0.0:8080/api/query?httpMethodRestrict=POST")
            .routeId("rag-query-endpoint")
            .log("Received RAG query via HTTP")
            
            // Parse incoming JSON query
            .process(exchange -> {
                String body = exchange.getIn().getBody(String.class);
                LOG.info("Query body: {}", body);
                
                // Extract query from JSON or use raw text
                String query;
                try {
                    Map<String, Object> jsonMap = objectMapper.readValue(body, Map.class);
                    query = (String) jsonMap.get("query");
                } catch (Exception e) {
                    query = body; // Fallback to raw text
                }
                
                if (query == null || query.trim().isEmpty()) {
                    throw new IllegalArgumentException("Query cannot be empty");
                }
                
                exchange.getIn().setHeader("userQuery", query);
                exchange.getIn().setBody(query);
            })
            
            // Search for relevant document chunks
            .process(exchange -> {
                String query = exchange.getIn().getHeader("userQuery", String.class);
                List<String> relevantChunks = vectorStoreService.searchRelevantChunks(query, MAX_RELEVANT_CHUNKS);
                
                LOG.info("Found {} relevant chunks for query", relevantChunks.size());
                
                // Store chunks in header for later use
                exchange.getIn().setHeader("relevantChunks", relevantChunks);
                
                // Build context from relevant chunks
                StringBuilder context = new StringBuilder();
                for (int i = 0; i < relevantChunks.size(); i++) {
                    context.append("Context ").append(i + 1).append(":\n");
                    context.append(relevantChunks.get(i)).append("\n\n");
                }
                
                exchange.getIn().setHeader("documentContext", context.toString());
            })
            
            // Build prompt for LLM
            .process(exchange -> {
                String query = exchange.getIn().getHeader("userQuery", String.class);
                String context = exchange.getIn().getHeader("documentContext", String.class);
                
                String prompt = buildRagPrompt(query, context);
                exchange.getIn().setBody(prompt);
                exchange.getIn().setHeader("prompt", prompt);
                
                LOG.info("Built RAG prompt with {} characters", prompt.length());
            })
            
            // Call LangChain4j chat component
            .to("langchain4j-chat:rag-chat?chatModel=#chatModel")
            
            // Format response
            .process(exchange -> {
                String aiResponse = exchange.getIn().getBody(String.class);
                List<String> chunks = exchange.getIn().getHeader("relevantChunks", List.class);
                
                Map<String, Object> response = new HashMap<>();
                response.put("answer", aiResponse);
                response.put("sources", chunks.size());
                response.put("timestamp", System.currentTimeMillis());
                
                String jsonResponse = objectMapper.writeValueAsString(response);
                
                exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
                exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "application/json");
                exchange.getIn().setBody(jsonResponse);
            })
            .log("RAG query completed successfully");
        
        // Route 2: Direct endpoint for programmatic queries
        from("direct:rag-query")
            .routeId("direct-rag-query")
            .log("Direct RAG query: ${body}")
            
            // Search for relevant chunks
            .process(exchange -> {
                String query = exchange.getIn().getBody(String.class);
                List<String> relevantChunks = vectorStoreService.searchRelevantChunks(query, MAX_RELEVANT_CHUNKS);
                
                StringBuilder context = new StringBuilder();
                for (String chunk : relevantChunks) {
                    context.append(chunk).append("\n\n");
                }
                
                String prompt = buildRagPrompt(query, context.toString());
                exchange.getIn().setBody(prompt);
            })
            
            // Call LangChain4j
            .to("langchain4j-chat:rag-chat?chatModel=#chatModel")
            
            .log("Direct RAG query completed: ${body}");
        
        // Route 3: Health check endpoint
        from("jetty:http://0.0.0.0:8080/api/health?httpMethodRestrict=GET")
            .routeId("health-check")
            .process(exchange -> {
                int embeddingsCount = vectorStoreService.getStoredEmbeddingsCount();
                
                Map<String, Object> health = new HashMap<>();
                health.put("status", "UP");
                health.put("embeddingsStored", embeddingsCount);
                health.put("timestamp", System.currentTimeMillis());
                
                String jsonResponse = objectMapper.writeValueAsString(health);
                
                exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
                exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "application/json");
                exchange.getIn().setBody(jsonResponse);
            });
    }
    
    /**
     * Build a RAG prompt that combines user query with retrieved context.
     */
    private String buildRagPrompt(String query, String context) {
        return String.format(
            "You are a helpful AI assistant. Answer the user's question based on the provided context.\n\n" +
            "Context:\n%s\n\n" +
            "Question: %s\n\n" +
            "Instructions:\n" +
            "- Provide a clear and concise answer based on the context\n" +
            "- If the context doesn't contain relevant information, say so\n" +
            "- Be specific and cite information from the context when possible\n\n" +
            "Answer:",
            context, query
        );
    }
}
