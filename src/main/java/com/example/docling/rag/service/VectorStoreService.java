package com.example.docling.rag.service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing vector embeddings and document storage.
 * Provides functionality to store document chunks as embeddings and retrieve relevant chunks.
 */
public class VectorStoreService {
    
    private static final Logger LOG = LoggerFactory.getLogger(VectorStoreService.class);
    private static final int MAX_CHUNK_SIZE = 500; // characters per chunk
    private static final int CHUNK_OVERLAP = 50;   // overlap between chunks
    
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final EmbeddingModel embeddingModel;
    private int storedEmbeddingsCount = 0;
    
    public VectorStoreService() {
        this.embeddingStore = new InMemoryEmbeddingStore<>();
        this.embeddingModel = new AllMiniLmL6V2EmbeddingModel();
        LOG.info("VectorStoreService initialized with in-memory store");
    }
    
    /**
     * Ingest a document by splitting it into chunks and storing embeddings.
     */
    public void ingestDocument(String documentText, String documentName) {
        LOG.info("Ingesting document: {}", documentName);
        
        // Split document into chunks
        List<String> chunks = splitIntoChunks(documentText);
        LOG.info("Document split into {} chunks", chunks.size());
        
        // Create text segments with metadata
        List<TextSegment> segments = new ArrayList<>();
        for (int i = 0; i < chunks.size(); i++) {
            Metadata metadata = Metadata.from("source", documentName)
                    .put("chunkIndex", i)
                    .put("totalChunks", chunks.size());
            segments.add(TextSegment.from(chunks.get(i), metadata));
        }
        
        // Generate embeddings and store
        for (TextSegment segment : segments) {
            Embedding embedding = embeddingModel.embed(segment).content();
            embeddingStore.add(embedding, segment);
            storedEmbeddingsCount++;
        }
        
        LOG.info("Successfully ingested {} chunks from document: {}", chunks.size(), documentName);
    }
    
    /**
     * Search for relevant document chunks based on a query.
     */
    public List<String> searchRelevantChunks(String query, int maxResults) {
        LOG.info("Searching for relevant chunks with query: {}", query);
        
        // Generate embedding for the query
        Embedding queryEmbedding = embeddingModel.embed(query).content();
        
        // Search for similar embeddings
        List<EmbeddingMatch<TextSegment>> matches = embeddingStore.search(
            dev.langchain4j.store.embedding.EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(maxResults)
                .minScore(0.0)
                .build()
        ).matches();
        
        // Extract text from matched segments
        List<String> relevantChunks = matches.stream()
                .map(match -> match.embedded().text())
                .collect(Collectors.toList());
        
        LOG.info("Found {} relevant chunks", relevantChunks.size());
        return relevantChunks;
    }
    
    /**
     * Split text into overlapping chunks for better context preservation.
     */
    private List<String> splitIntoChunks(String text) {
        List<String> chunks = new ArrayList<>();
        
        if (text == null || text.isEmpty()) {
            return chunks;
        }
        
        int textLength = text.length();
        int start = 0;
        
        while (start < textLength) {
            int end = Math.min(start + MAX_CHUNK_SIZE, textLength);
            
            // Try to break at sentence boundary if possible
            if (end < textLength) {
                int lastPeriod = text.lastIndexOf('.', end);
                int lastNewline = text.lastIndexOf('\n', end);
                int breakPoint = Math.max(lastPeriod, lastNewline);
                
                if (breakPoint > start + (MAX_CHUNK_SIZE / 2)) {
                    end = breakPoint + 1;
                }
            }
            
            chunks.add(text.substring(start, end).trim());
            start = end - CHUNK_OVERLAP;
        }
        
        return chunks;
    }
    
    /**
     * Get the current number of stored embeddings.
     */
    public int getStoredEmbeddingsCount() {
        return storedEmbeddingsCount;
    }
}
