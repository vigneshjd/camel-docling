# Camel Docling RAG - Project Summary

## Overview

Successfully created a complete RAG (Retrieval-Augmented Generation) system using:
- **Apache Camel Docling Component** - For parsing and extracting text from various document formats
- **Apache Camel LangChain4j-Chat Component** - For AI-powered query responses
- **LangChain4j Embeddings** - For vector embeddings and semantic search
- **In-Memory Vector Store** - For storing and retrieving document embeddings

## Project Structure

```
camel-docling/
├── src/main/java/com/example/docling/rag/
│   ├── DoclingRagApplication.java           # Main application entry point
│   ├── DocumentIngestionRoute.java          # Camel routes for document ingestion
│   ├── RagQueryRoute.java                   # Camel routes for RAG queries
│   ├── config/
│   │   └── ChatModelFactory.java            # LLM model configuration
│   ├── service/
│   │   └── VectorStoreService.java          # Vector store management
│   └── example/
│       └── ProgrammaticUsageExample.java    # Usage examples
│
├── src/main/resources/
│   └── application.properties               # Application configuration
│
├── data/
│   ├── input/                               # Auto-watch directory for documents
│   ├── output/                              # Processed documents output
│   ├── failed/                              # Failed processing attempts
│   └── sample-document.md                   # Sample document for testing
│
├── build.gradle                             # Gradle build configuration
├── settings.gradle                          # Gradle settings
├── gradlew.bat                              # Gradle wrapper (Windows)
├── test-ingest.bat                          # Test script for ingestion
├── test-query.bat                           # Test script for queries
├── README.md                                # Comprehensive documentation
├── QUICKSTART.md                            # Quick start guide
└── API.md                                   # API documentation
```

## Key Features Implemented

### 1. Document Ingestion
- **File System Watcher**: Automatically processes documents placed in `data/input/`
- **HTTP Upload API**: POST endpoint for uploading documents programmatically
- **Direct Integration**: Camel direct endpoint for in-application usage
- **Multiple Formats**: Supports PDF, DOCX, TXT, MD, HTML, and more via Docling

### 2. Vector Storage
- **Automatic Chunking**: Splits documents into manageable chunks (500 chars with 50 char overlap)
- **Semantic Embeddings**: Uses AllMiniLmL6V2 model for generating embeddings
- **In-Memory Store**: Fast retrieval with in-memory vector database
- **Metadata Tracking**: Stores source document and chunk information

### 3. RAG Query System
- **Context Retrieval**: Finds top-K most relevant document chunks
- **Prompt Engineering**: Builds optimized prompts with retrieved context
- **LLM Integration**: Uses OpenAI GPT-3.5-turbo (or mock model for testing)
- **HTTP Query API**: POST endpoint for asking questions
- **JSON Responses**: Structured responses with answers and metadata

### 4. REST API
Three main endpoints:
- `GET /api/health` - System health check
- `POST /api/ingest` - Document upload and ingestion
- `POST /api/query` - RAG query processing

## Technology Stack

### Apache Camel Components
- `camel-core` - Core routing and mediation
- `camel-main` - Standalone application support
- `camel-docling` - Document parsing component (NEW!)
- `camel-langchain4j-chat` - AI chat integration (NEW!)
- `camel-langchain4j-embeddings` - Vector embeddings
- `camel-jackson` - JSON processing
- `camel-jetty` - HTTP server
- `camel-file` - File system operations

### LangChain4j Libraries
- `langchain4j` - Core LangChain4j library
- `langchain4j-open-ai` - OpenAI integration
- `langchain4j-embeddings-all-minilm-l6-v2` - Embedding model
- In-memory vector store

### Other Dependencies
- SLF4J for logging
- Jackson for JSON
- JUnit 5 for testing

## How It Works

### Document Ingestion Flow
1. Document arrives (file system, HTTP, or direct)
2. Camel Docling component parses the document
3. Text is extracted in Markdown format
4. VectorStoreService splits text into chunks
5. Each chunk is converted to embeddings
6. Embeddings stored with metadata in vector database

### RAG Query Flow
1. User submits a question
2. Question is converted to an embedding
3. Vector similarity search finds relevant chunks
4. Top-K chunks are retrieved as context
5. Prompt is built with question + context
6. Camel-langchain4j-chat sends to LLM
7. AI-generated answer is returned

## Configuration Options

### Application Properties
```properties
camel.main.name=DoclingRagApplication
camel.main.duration-max-seconds=0
camel.component.jetty.port=8080

document.chunk.size=500
document.chunk.overlap=50
document.max.relevant.chunks=5
```

### Environment Variables
- `OPENAI_API_KEY` - OpenAI API key for production use

## Quick Start Commands

```cmd
# Build the project
gradlew build

# Run the application
gradlew run

# Test document ingestion
test-ingest.bat

# Test RAG queries
test-query.bat

# Manual health check
curl http://localhost:8080/api/health
```

## Usage Examples

### Ingest a Document
```bash
curl -X POST http://localhost:8080/api/ingest \
  -H "Content-Type: application/pdf" \
  -H "documentName: report.pdf" \
  --data-binary @report.pdf
```

### Query the System
```bash
curl -X POST http://localhost:8080/api/query \
  -H "Content-Type: application/json" \
  -d "{\"query\": \"What are the key findings?\"}"
```

### Check Health
```bash
curl http://localhost:8080/api/health
```

## Testing

### Provided Test Scripts
1. **test-ingest.bat** - Tests document ingestion
2. **test-query.bat** - Tests multiple RAG queries
3. Sample document included for immediate testing

### Manual Testing
1. Copy documents to `data/input/` directory
2. Watch application logs for processing
3. Query via HTTP API or test scripts

## Key Classes

### DoclingRagApplication
- Main entry point
- Configures Camel context
- Registers chat model
- Starts HTTP server

### DocumentIngestionRoute
- Defines ingestion routes
- Integrates Docling component
- Manages vector storage
- Handles multiple input methods

### RagQueryRoute
- Defines query routes
- Performs similarity search
- Builds RAG prompts
- Integrates LangChain4j chat

### VectorStoreService
- Manages embeddings
- Performs chunking
- Stores and retrieves vectors
- Tracks document metadata

### ChatModelFactory
- Configures LLM
- Supports OpenAI
- Provides mock fallback

## Extensibility

### Add New Document Sources
```java
from("database:table")
    .to("docling:parse")
    .to("direct:ingest-document");
```

### Use Different Vector Store
Replace InMemoryEmbeddingStore with:
- ChromaDB
- Pinecone
- Weaviate
- PostgreSQL + pgvector

### Use Different LLM
Modify ChatModelFactory to use:
- Azure OpenAI
- Anthropic Claude
- Google PaLM
- Local models (Ollama)

## Production Considerations

### Scaling
- Replace in-memory store with persistent vector DB
- Add connection pooling for LLM API calls
- Implement request queuing for large documents
- Use distributed caching

### Security
- Add API authentication (API keys, OAuth)
- Implement rate limiting
- Validate and sanitize inputs
- Encrypt sensitive data

### Monitoring
- Add metrics collection (Prometheus)
- Implement distributed tracing
- Set up alerting for failures
- Monitor token usage and costs

### Performance
- Optimize chunk size for your use case
- Batch document processing
- Cache frequently accessed embeddings
- Use async processing for large documents

## Troubleshooting

### Common Issues
1. **Port 8080 in use**: Change port in application.properties
2. **No Java**: Install Java 17 or higher
3. **OpenAI errors**: Check API key and quota
4. **Empty responses**: Ensure documents are ingested first
5. **Parse failures**: Verify document format is supported

### Logs Location
Application logs are output to console. Configure file logging in application.properties if needed.

## Next Steps

1. **Add More Documents**: Place PDFs, Word docs, or text files in `data/input/`
2. **Customize Prompts**: Edit RagQueryRoute to adjust prompt templates
3. **Add Authentication**: Implement security for production use
4. **Persist Vector Store**: Switch to persistent storage
5. **Deploy**: Package as Docker container or deploy to cloud

## Documentation Files

- **README.md** - Comprehensive project documentation
- **QUICKSTART.md** - 5-minute quick start guide
- **API.md** - Complete API reference with examples
- **This file** - Project summary and overview

## Success Criteria ✓

✓ Camel Docling component integrated for document parsing
✓ Camel LangChain4j-chat component integrated for AI responses
✓ Vector store implemented with embeddings
✓ RAG query system fully functional
✓ HTTP REST API endpoints working
✓ File system watcher implemented
✓ Test scripts provided
✓ Sample document included
✓ Comprehensive documentation created
✓ Quick start guide available
✓ API documentation complete
✓ Error handling implemented
✓ Logging configured
✓ Build system set up (Gradle)

## Project Status: ✅ COMPLETE

The Camel Docling RAG project is fully implemented and ready to use!

To get started right now:
```cmd
cd "C:\Technical\Camel Workspace\camel-docling"
gradlew run
```

Then in another terminal:
```cmd
test-ingest.bat
test-query.bat
```

Enjoy building intelligent document analysis applications with Apache Camel!
