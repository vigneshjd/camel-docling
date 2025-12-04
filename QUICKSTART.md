# Quick Start Guide - Camel Docling RAG

This guide will help you get the Camel Docling RAG system up and running in 5 minutes.

## Prerequisites

- Java 17 or higher installed
- Internet connection (for downloading dependencies)

## Step 1: Navigate to Project Directory

```cmd
cd "C:\Technical\Camel Workspace\camel-docling"
```

## Step 2: Build the Project

```cmd
gradlew build
```

This will download all dependencies and compile the project.

## Step 3: Run the Application

```cmd
gradlew run
```

You should see output indicating the application has started:
```
Starting Camel Docling RAG Application...
HTTP endpoints available:
  - POST http://localhost:8080/api/ingest - Upload and ingest documents
  - POST http://localhost:8080/api/query - Query the RAG system
  - GET  http://localhost:8080/api/health - Check system health
```

## Step 4: Test the System

### Option A: Using Test Scripts (Recommended)

Open a new command prompt and run:

1. **Ingest the sample document:**
   ```cmd
   test-ingest.bat
   ```

2. **Query the system:**
   ```cmd
   test-query.bat
   ```

### Option B: Manual Testing with curl

1. **Check health:**
   ```cmd
   curl http://localhost:8080/api/health
   ```

2. **Ingest a document:**
   ```cmd
   curl -X POST http://localhost:8080/api/ingest -H "Content-Type: text/markdown" -H "documentName: sample-doc.md" --data-binary @data/sample-document.md
   ```

3. **Query the system:**
   ```cmd
   curl -X POST http://localhost:8080/api/query -H "Content-Type: application/json" -d "{\"query\": \"What is cloud computing?\"}"
   ```

### Option C: File System Watcher

Simply copy documents into the `data/input` directory:
```cmd
copy your-document.pdf data\input\
```

The system will automatically process them!

## Step 5: (Optional) Configure OpenAI

For production-quality AI responses, set your OpenAI API key:

```cmd
set OPENAI_API_KEY=your-api-key-here
```

Then restart the application.

## What's Next?

- Read the full [README.md](README.md) for detailed documentation
- Add your own documents to the `data/input` directory
- Customize the configuration in `src/main/resources/application.properties`
- Explore the code to understand how RAG works

## Troubleshooting

### Port 8080 already in use
Edit `application.properties` and change:
```properties
camel.component.jetty.port=8081
```

### Build fails
Ensure you have Java 17 or higher:
```cmd
java -version
```

### No response from queries
1. Make sure you've ingested documents first
2. Check the application logs for errors
3. Verify the health endpoint shows embeddings are stored

## Common Commands

| Action | Command |
|--------|---------|
| Build | `gradlew build` |
| Run | `gradlew run` |
| Clean | `gradlew clean` |
| Test | `gradlew test` |
| Stop | Press `Ctrl+C` in the terminal |

## Architecture Overview

```
Your Documents → Docling Parser → Text Chunking → Vector Embeddings → Vector Store
                                                                            ↓
Your Questions ← AI Response ← LangChain4j LLM ← Context Assembly ← Similarity Search
```

Enjoy building with Camel Docling RAG!
