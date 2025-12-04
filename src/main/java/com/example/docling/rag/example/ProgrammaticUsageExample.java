package com.example.docling.rag.example;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Example class demonstrating programmatic usage of the Docling RAG system.
 * This shows how to integrate the RAG system into other applications.
 */
public class ProgrammaticUsageExample {
    
    private static final Logger LOG = LoggerFactory.getLogger(ProgrammaticUsageExample.class);
    
    public static void main(String[] args) {
        try {
            // This example shows how to use the RAG system programmatically
            // In a real scenario, you would get the CamelContext from your running application
            
            CamelContext camelContext = new DefaultCamelContext();
            ProducerTemplate template = camelContext.createProducerTemplate();
            
            camelContext.start();
            
            LOG.info("=== Programmatic RAG Usage Example ===");
            
            // Example 1: Ingest a document directly
            String documentContent = "Apache Camel is an open-source integration framework based on " +
                    "known Enterprise Integration Patterns. It provides a rule-based routing and " +
                    "mediation engine with a comprehensive component ecosystem.";
            
            LOG.info("\n1. Ingesting document programmatically...");
            template.sendBodyAndHeader(
                    "direct:ingest-document",
                    documentContent,
                    "documentName", "apache-camel-intro"
            );
            
            // Wait for ingestion to complete
            Thread.sleep(2000);
            
            // Example 2: Query the RAG system
            LOG.info("\n2. Querying RAG system...");
            String query = "What is Apache Camel?";
            String response = template.requestBody("direct:rag-query", query, String.class);
            
            LOG.info("\nQuery: {}", query);
            LOG.info("Response: {}", response);
            
            // Example 3: Another query
            LOG.info("\n3. Second query...");
            query = "What are Enterprise Integration Patterns?";
            response = template.requestBody("direct:rag-query", query, String.class);
            
            LOG.info("\nQuery: {}", query);
            LOG.info("Response: {}", response);
            
            camelContext.stop();
            
            LOG.info("\n=== Example completed successfully ===");
            
        } catch (Exception e) {
            LOG.error("Error in programmatic example", e);
        }
    }
}
