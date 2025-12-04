# Camel Docling RAG Application

A Retrieval-Augmented Generation (RAG) system built with Apache Camel, using the camel-docling component for document parsing and camel-langchain4j-chat for AI-powered insights.

## Features

- **Document Ingestion**: Automatically parse and process documents using Apache Camel Docling component
- **Vector Storage**: Store document embeddings using LangChain4j's embedding models
- **RAG Queries**: Ask questions about your documents and get AI-powered answers
- **Multiple Input Methods**: 
  - File system watcher for automatic ingestion
  - HTTP REST API for document upload
  - Direct programmatic access
- **Flexible AI Backend**: Supports OpenAI or mock responses for testing

## Architecture

```
┌─────────────────┐
│   Documents     │
│  (PDF, DOCX,    │
│   TXT, etc.)    │
└────────┬────────┘
         │
         ▼
┌─────────────────────────────┐
│  Camel Docling Component    │
│  (Document Parsing)         │
└────────┬────────────────────┘
         │
         ▼
┌─────────────────────────────┐
│  Text Chunking & Embedding  │
│  (LangChain4j)              │
└────────┬────────────────────┘
         │
         ▼
┌─────────────────────────────┐
│  In-Memory Vector Store     │
└────────┬────────────────────┘
         │
         ▼
┌─────────────────────────────┐
│  RAG Query Processing       │
│  - Similarity Search        │
│  - Context Retrieval        │
└────────┬────────────────────┘
         │
         ▼
┌─────────────────────────────┐
│  LangChain4j Chat Model     │
│  (OpenAI GPT-3.5-turbo)     │
└────────┬────────────────────┘
         │
         ▼
┌─────────────────────────────┐
│  AI-Powered Answer          │
└─────────────────────────────┘
```

## Prerequisites

- Java 17 or higher
- Gradle 7.x or higher
- (Optional) OpenAI API key for production use

## Getting Started

### 1. Clone and Build

```bash
cd camel-docling
gradlew build
```

### 2. Configure OpenAI (Optional)

For production use with real AI responses, set your OpenAI API key:

**Windows (cmd.exe):**
```cmd
set OPENAI_API_KEY=your-api-key-here
```

**Windows (PowerShell):**
```powershell
$env:OPENAI_API_KEY="your-api-key-here"
```

**Linux/Mac:**
```bash
export OPENAI_API_KEY=your-api-key-here
```

If no API key is set, the application will use a mock chat model for testing.

### 3. Run the Application

```bash
gradlew run
```

The application will start and listen on port 8080.

## Usage

### Method 1: File System Watcher

Place documents in the `data/input` directory. They will be automatically:
1. Parsed by the Docling component
2. Split into chunks
3. Embedded and stored in the vector database

```bash
# Create the input directory if it doesn't exist
mkdir data\input

# Copy your documents
copy your-document.pdf data\input\
```

### Method 2: HTTP API Upload

Upload documents via HTTP POST:

```bash
curl -X POST http://localhost:8080/api/ingest \
  -H "Content-Type: application/pdf" \
  -H "documentName: my-document.pdf" \
  --data-binary @your-document.pdf
```

### Query the RAG System

Ask questions about your ingested documents:

```bash
curl -X POST http://localhost:8080/api/query \
  -H "Content-Type: application/json" \
  -d "{\"query\": \"What are the main topics discussed in the documents?\"}"
```

### Check System Health

```bash
curl http://localhost:8080/api/health
```

Response:
```json
{
  "status": "UP",
  "embeddingsStored": 42,
  "timestamp": 1701612345678
}
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/ingest` | Upload and ingest a document |
| POST | `/api/query` | Query the RAG system |
| GET | `/api/health` | Check system health |

## Project Structure

```
camel-docling/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/example/docling/rag/
│       │       ├── DoclingRagApplication.java      # Main application
│       │       ├── DocumentIngestionRoute.java      # Document ingestion routes
│       │       ├── RagQueryRoute.java               # RAG query routes
│       │       ├── config/
│       │       │   └── ChatModelFactory.java        # LLM configuration
│       │       └── service/
│       │           └── VectorStoreService.java      # Vector storage service
│       └── resources/
│           └── application.properties               # Configuration
├── data/
│   ├── input/                                       # Document input directory
│   ├── output/                                      # Processing results
│   └── failed/                                      # Failed documents
├── build.gradle                                     # Gradle build file
└── README.md                                        # This file
```

## How It Works

### Document Ingestion Flow

1. **Document Input**: Documents are received via file watcher or HTTP upload
2. **Parsing**: Camel Docling component parses the document (PDF, DOCX, TXT, etc.)
3. **Text Extraction**: Raw text is extracted in Markdown format
4. **Chunking**: Text is split into overlapping chunks (default 500 chars with 50 char overlap)
5. **Embedding**: Each chunk is converted to a vector embedding using AllMiniLmL6V2 model
6. **Storage**: Embeddings are stored in an in-memory vector store with metadata

### RAG Query Flow

1. **Query Input**: User submits a question via HTTP or direct API
2. **Query Embedding**: Question is converted to a vector embedding
3. **Similarity Search**: Vector store finds the most relevant document chunks (default top 5)
4. **Context Building**: Relevant chunks are assembled into context
5. **Prompt Construction**: Query and context are formatted into an LLM prompt
6. **LLM Processing**: Camel-langchain4j-chat component sends prompt to the language model
7. **Response**: AI-generated answer is returned with source information

## Configuration

Edit `src/main/resources/application.properties` to customize:

```properties
# Chunk size for document splitting
document.chunk.size=500

# Overlap between chunks
document.chunk.overlap=50

# Number of relevant chunks to retrieve
document.max.relevant.chunks=5
```

## Supported Document Formats

The Docling component supports various formats including:
- PDF
- Microsoft Word (DOCX)
- Plain Text (TXT)
- Markdown (MD)
- And more...

## Extending the Application

### Adding New Document Sources

Add new Camel routes in `DocumentIngestionRoute.java`:

```java
from("ftp://server/documents")
    .to("docling:parse")
    .to("direct:ingest-document");
```

### Using Different Vector Stores

Replace `InMemoryEmbeddingStore` in `VectorStoreService.java` with:
- ChromaDB
- Pinecone
- Weaviate
- PostgreSQL with pgvector

### Using Different LLM Models

Modify `ChatModelFactory.java` to use:
- Azure OpenAI
- Anthropic Claude
- Google Palm
- Local models via Ollama

## Troubleshooting

### Documents not processing
- Check the `data/failed` directory for failed documents
- Review logs for parsing errors
- Verify document format is supported by Docling

### Low quality answers
- Increase `document.max.relevant.chunks` to provide more context
- Adjust chunk size and overlap for better segmentation
- Ensure documents are properly ingested (check health endpoint)

### API Key errors
- Verify OPENAI_API_KEY environment variable is set
- Check API key validity and quota
- Review application logs for authentication errors

## Performance Considerations

- **In-Memory Storage**: Current implementation uses in-memory vector store. For production, consider persistent storage.
- **Embedding Model**: Using lightweight AllMiniLmL6V2 model. For better accuracy, consider larger models.
- **Scalability**: For high-volume scenarios, consider distributed vector databases and load balancing.

## License

This project is provided as-is for demonstration purposes.

## Contributing

Feel free to submit issues and enhancement requests!

## Support

For questions and support, please refer to:
- [Apache Camel Documentation](https://camel.apache.org/components/latest/docling-component.html)
- [LangChain4j Documentation](https://docs.langchain4j.dev/)
