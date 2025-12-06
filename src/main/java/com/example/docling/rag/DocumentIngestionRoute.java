package com.example.docling.rag;

import com.example.docling.rag.service.VectorStoreService;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.docling.DoclingConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Camel route for ingesting documents using the docling component.
 * This route watches a directory for new documents, parses them using docling,
 * and stores the extracted text in a vector store.
 */
public class DocumentIngestionRoute extends RouteBuilder {
    
    private static final Logger LOG = LoggerFactory.getLogger(DocumentIngestionRoute.class);
    private final VectorStoreService vectorStoreService = new VectorStoreService();
    
    @Override
    public void configure() throws Exception {
        
        // Bind the VectorStoreService to the registry
        getContext().getRegistry().bind("vectorStoreService", vectorStoreService);
        
        // Route 1: Watch directory for new documents and ingest them
        from("file:data/input?delete=true&moveFailed=data/failed")
            .routeId("document-ingestion")
            .log("Processing document: ${header.CamelFileName}")
            
            // Parse document using docling component
            .to("docling:parse?outputFormat=markdown")
            
            // Extract the parsed text
            .process(exchange -> {
                String fileName = exchange.getIn().getHeader(Exchange.FILE_NAME, String.class);
                String parsedText = exchange.getIn().getBody(String.class);
                
                LOG.info("Document {} parsed successfully. Text length: {}", fileName, parsedText != null ? parsedText.length() : 0);
                
                // Store in vector store
                if (parsedText != null && !parsedText.isEmpty()) {
                    vectorStoreService.ingestDocument(parsedText, fileName);
                    exchange.getIn().setBody("Document ingested successfully: " + fileName);
                } else {
                    throw new IllegalStateException("Parsed document text is empty");
                }
            })
            
            // Save processing result
            .to("file:data/output?fileName=${header.CamelFileName}.processed.txt")
            .log("Document ${header.CamelFileName} ingested successfully. Total embeddings: ${body}");
        
        // Route 2: Direct endpoint for programmatic document ingestion
        from("direct:ingest-document")
            .routeId("direct-document-ingestion")
            .log("Direct ingestion request received")
            
            // Parse using docling
            .to("docling:parse?outputFormat=markdown")
            
            // Store in vector store
            .process(exchange -> {
                String fileName = exchange.getIn().getHeader("documentName", String.class);
                if (fileName == null) {
                    fileName = "document-" + System.currentTimeMillis();
                }
                
                String parsedText = exchange.getIn().getBody(String.class);
                vectorStoreService.ingestDocument(parsedText, fileName);
                
                exchange.getIn().setBody("Successfully ingested: " + fileName);
            })
            .log("Direct ingestion completed: ${body}");
        
        // Route 3: HTTP endpoint for document upload
        from("undertow:http://0.0.0.0:8080/api/ingest?httpMethodRestrict=POST")
            .routeId("http-document-upload")
            .log("Document upload received via HTTP")
            
            // Extract document name from header
            .setHeader("documentName", simple("${header.documentName}"))
            
            // Parse with docling
            .to("docling:parse?outputFormat=markdown")
            
            // Ingest into vector store
            .process(exchange -> {
                String docName = exchange.getIn().getHeader("documentName", String.class);
                if (docName == null || docName.isEmpty()) {
                    docName = "uploaded-doc-" + System.currentTimeMillis();
                }
                
                String parsedText = exchange.getIn().getBody(String.class);
                vectorStoreService.ingestDocument(parsedText, docName);
                
                exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
                exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "application/json");
                exchange.getIn().setBody("{\"status\":\"success\",\"message\":\"Document ingested: " + docName + "\",\"embeddings\":" + vectorStoreService.getStoredEmbeddingsCount() + "}");
            })
            .log("HTTP ingestion completed");
    }
}
