package com.example.docling.rag;

import com.example.docling.rag.config.ChatModelFactory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.apache.camel.main.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main application class for Camel Docling RAG system.
 * This application demonstrates how to build a RAG (Retrieval-Augmented Generation) system
 * using Apache Camel's docling component for document parsing and langchain4j-chat for AI interactions.
 */
public class DoclingRagApplication {
    
    private static final Logger LOG = LoggerFactory.getLogger(DoclingRagApplication.class);
    
    public static void main(String[] args) throws Exception {
        LOG.info("Starting Camel Docling RAG Application...");
        
        Main main = new Main();
        
        // Create and bind chat model
        ChatLanguageModel chatModel = ChatModelFactory.createChatModel();
        main.bind("chatModel", chatModel);
        LOG.info("Chat model configured and bound to registry");
        
        // Configure the application
        main.configure().addRoutesBuilder(new DocumentIngestionRoute());
        main.configure().addRoutesBuilder(new RagQueryRoute());
        
        LOG.info("Routes configured successfully");
        LOG.info("HTTP endpoints available:");
        LOG.info("  - POST http://localhost:8080/api/ingest - Upload and ingest documents");
        LOG.info("  - POST http://localhost:8080/api/query - Query the RAG system");
        LOG.info("  - GET  http://localhost:8080/api/health - Check system health");
        LOG.info("File watcher: Place documents in ./data/input directory for automatic ingestion");
        
        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                LOG.info("Shutting down Camel Docling RAG Application...");
                main.stop();
            } catch (Exception e) {
                LOG.error("Error during shutdown", e);
            }
        }));
        
        // Start the application
        main.run(args);
    }
}
