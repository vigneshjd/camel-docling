# API Documentation - Camel Docling RAG

This document provides detailed information about the REST API endpoints available in the Camel Docling RAG system.

## Base URL

```
http://localhost:8080/api
```

## Endpoints

### 1. Health Check

Check the health and status of the RAG system.

**Endpoint:** `GET /api/health`

**Request:**
```bash
curl http://localhost:8080/api/health
```

**Response:**
```json
{
  "status": "UP",
  "embeddingsStored": 42,
  "timestamp": 1701612345678
}
```

**Response Fields:**
- `status`: System status (UP/DOWN)
- `embeddingsStored`: Number of document embeddings currently stored
- `timestamp`: Current server timestamp in milliseconds

---

### 2. Document Ingestion

Upload and ingest a document into the RAG system.

**Endpoint:** `POST /api/ingest`

**Headers:**
- `Content-Type`: The MIME type of the document (e.g., `application/pdf`, `text/plain`, `text/markdown`)
- `documentName` (optional): Custom name for the document

**Request Example (Markdown):**
```bash
curl -X POST http://localhost:8080/api/ingest \
  -H "Content-Type: text/markdown" \
  -H "documentName: my-document.md" \
  --data-binary @path/to/document.md
```

**Request Example (PDF):**
```bash
curl -X POST http://localhost:8080/api/ingest \
  -H "Content-Type: application/pdf" \
  -H "documentName: report.pdf" \
  --data-binary @path/to/report.pdf
```

**Success Response:**
```json
{
  "status": "success",
  "message": "Document ingested: my-document.md",
  "embeddings": 45
}
```

**Response Fields:**
- `status`: Operation status (success/error)
- `message`: Descriptive message about the operation
- `embeddings`: Total number of embeddings now stored in the system

**Error Response:**
```json
{
  "status": "error",
  "message": "Failed to parse document",
  "error": "Unsupported document format"
}
```

**Supported Document Formats:**
- PDF (`.pdf`)
- Microsoft Word (`.docx`)
- Plain Text (`.txt`)
- Markdown (`.md`)
- HTML (`.html`)
- And more formats supported by Docling

---

### 3. RAG Query

Query the RAG system with a question and receive an AI-powered answer based on ingested documents.

**Endpoint:** `POST /api/query`

**Headers:**
- `Content-Type`: `application/json`

**Request Body:**
```json
{
  "query": "What is cloud computing?"
}
```

**Request Example:**
```bash
curl -X POST http://localhost:8080/api/query \
  -H "Content-Type: application/json" \
  -d "{\"query\": \"What is cloud computing?\"}"
```

**Success Response:**
```json
{
  "answer": "Cloud computing is a technology paradigm that enables on-demand access to shared pools of configurable computing resources. It provides fundamental characteristics like on-demand self-service, broad network access, and resource pooling...",
  "sources": 5,
  "timestamp": 1701612345678
}
```

**Response Fields:**
- `answer`: The AI-generated answer based on document context
- `sources`: Number of relevant document chunks used to generate the answer
- `timestamp`: Response timestamp in milliseconds

**Error Response (No Documents):**
```json
{
  "error": "No documents have been ingested yet. Please ingest documents before querying.",
  "timestamp": 1701612345678
}
```

**Error Response (Empty Query):**
```json
{
  "error": "Query cannot be empty",
  "timestamp": 1701612345678
}
```

---

## Complete Usage Examples

### Example 1: Complete Workflow

```bash
# Step 1: Check system health
curl http://localhost:8080/api/health

# Step 2: Ingest a document
curl -X POST http://localhost:8080/api/ingest \
  -H "Content-Type: text/plain" \
  -H "documentName: tech-overview.txt" \
  --data-binary @tech-overview.txt

# Step 3: Query the system
curl -X POST http://localhost:8080/api/query \
  -H "Content-Type: application/json" \
  -d "{\"query\": \"Summarize the key technologies mentioned\"}"
```

### Example 2: Multiple Documents

```bash
# Ingest multiple documents
curl -X POST http://localhost:8080/api/ingest \
  -H "Content-Type: application/pdf" \
  -H "documentName: doc1.pdf" \
  --data-binary @doc1.pdf

curl -X POST http://localhost:8080/api/ingest \
  -H "Content-Type: text/markdown" \
  -H "documentName: doc2.md" \
  --data-binary @doc2.md

# Query across all documents
curl -X POST http://localhost:8080/api/query \
  -H "Content-Type: application/json" \
  -d "{\"query\": \"What are the common themes across these documents?\"}"
```

### Example 3: Detailed Questions

```bash
# Ask specific questions
curl -X POST http://localhost:8080/api/query \
  -H "Content-Type: application/json" \
  -d "{\"query\": \"What are the benefits mentioned?\"}"

curl -X POST http://localhost:8080/api/query \
  -H "Content-Type: application/json" \
  -d "{\"query\": \"List all the technologies discussed\"}"

curl -X POST http://localhost:8080/api/query \
  -H "Content-Type: application/json" \
  -d "{\"query\": \"What are the security considerations?\"}"
```

---

## HTTP Status Codes

| Status Code | Description |
|-------------|-------------|
| 200 | Success - Request processed successfully |
| 400 | Bad Request - Invalid input or missing parameters |
| 500 | Internal Server Error - Server-side error occurred |

---

## Rate Limiting

Currently, there is no rate limiting implemented. For production use, consider implementing:
- Request throttling
- API key authentication
- Usage quotas per client

---

## Authentication

The current implementation does not require authentication. For production deployment, consider adding:
- API keys
- OAuth 2.0
- JWT tokens
- Basic authentication

---

## Best Practices

### Document Ingestion

1. **Use descriptive document names**: Include meaningful identifiers in the `documentName` header
2. **Batch ingestion**: If you have many documents, ingest them sequentially with delays
3. **Check status**: Always check the health endpoint after ingestion to verify success
4. **Supported formats**: Use supported document formats for best results

### Querying

1. **Be specific**: More specific questions yield better answers
2. **Wait for ingestion**: Ensure documents are ingested before querying
3. **Iterate queries**: Refine your questions based on initial responses
4. **Context aware**: Remember the system answers based on ingested documents only

### Performance

1. **Document size**: Large documents may take longer to process
2. **Query complexity**: Complex queries require more processing time
3. **Concurrent requests**: The system handles concurrent requests, but performance may vary

---

## Error Handling

### Common Errors and Solutions

**Error: Connection refused**
```
Solution: Ensure the application is running on port 8080
Command: gradlew run
```

**Error: Document parsing failed**
```
Solution: Verify the document format is supported and file is not corrupted
```

**Error: Empty response**
```
Solution: Check that documents have been ingested and contain relevant content
```

**Error: Timeout**
```
Solution: Large documents may take time to process. Increase timeout or split documents
```

---

## Monitoring and Debugging

### Check Logs

Application logs provide detailed information:
```bash
# Watch logs while running
gradlew run
```

### Health Monitoring

Regularly check the health endpoint:
```bash
# Simple health check script
while true; do
  curl http://localhost:8080/api/health
  sleep 60
done
```

### Verify Embeddings

Check the health endpoint to see stored embeddings count:
```bash
curl http://localhost:8080/api/health | grep embeddingsStored
```

---

## Integration Examples

### Python Integration

```python
import requests
import json

# Base URL
base_url = "http://localhost:8080/api"

# Ingest document
with open('document.pdf', 'rb') as f:
    response = requests.post(
        f"{base_url}/ingest",
        headers={
            'Content-Type': 'application/pdf',
            'documentName': 'document.pdf'
        },
        data=f
    )
    print(response.json())

# Query
query_data = {"query": "What is the main topic?"}
response = requests.post(
    f"{base_url}/query",
    headers={'Content-Type': 'application/json'},
    json=query_data
)
print(response.json()['answer'])
```

### JavaScript Integration

```javascript
// Ingest document
async function ingestDocument(file, filename) {
  const response = await fetch('http://localhost:8080/api/ingest', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/pdf',
      'documentName': filename
    },
    body: file
  });
  return await response.json();
}

// Query
async function query(question) {
  const response = await fetch('http://localhost:8080/api/query', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ query: question })
  });
  return await response.json();
}

// Usage
query("What is discussed in the document?")
  .then(result => console.log(result.answer));
```

### Java Integration

```java
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

public class RagClient {
    private static final String BASE_URL = "http://localhost:8080/api";
    private static final HttpClient client = HttpClient.newHttpClient();
    
    public static String query(String question) throws Exception {
        String json = "{\"query\":\"" + question + "\"}";
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/query"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();
            
        HttpResponse<String> response = client.send(request, 
            HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}
```

---

## WebSocket Support (Future)

WebSocket support for streaming responses is planned for future versions. This will enable:
- Real-time query responses
- Progress updates during document ingestion
- Streaming AI responses for better UX

---

## API Versioning

Current version: **v1** (implicit in `/api/` prefix)

Future versions will be explicitly versioned:
- `/api/v1/query`
- `/api/v2/query`

---

## Support and Feedback

For API issues or feature requests:
1. Check the application logs
2. Review the README.md for configuration options
3. Consult the QUICKSTART.md for setup issues

---

## Changelog

### Version 1.0.0 (Current)
- Initial release
- Document ingestion via HTTP
- RAG query endpoint
- Health check endpoint
- Support for multiple document formats
- LangChain4j integration
- Camel Docling component integration
