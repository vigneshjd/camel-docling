# 📚 Camel Docling RAG - Complete Index

Welcome to the Camel Docling RAG project! This index will help you navigate the project and get started quickly.

## 🚀 Quick Start (Choose Your Path)

### Path 1: I'm New Here (5 minutes)
1. Open and read: **[QUICKSTART.md](QUICKSTART.md)**
2. Run: `verify-setup.bat`
3. Run: `build-and-run.bat`
4. Run: `test-ingest.bat` (in another terminal)
5. Run: `test-query.bat`

### Path 2: I Want Details (15 minutes)
1. Read: **[README.md](README.md)** - Complete documentation
2. Read: **[PROJECT_SUMMARY.md](PROJECT_SUMMARY.md)** - Architecture overview
3. Read: **[API.md](API.md)** - API reference
4. Explore the source code in `src/main/java/`

### Path 3: I Just Want to Run It (2 minutes)
```cmd
verify-setup.bat
build-and-run.bat
```

## 📖 Documentation Files

| File | Purpose | Read Time |
|------|---------|-----------|
| **QUICKSTART.md** | Get started in 5 minutes | 5 min |
| **README.md** | Comprehensive documentation | 15 min |
| **API.md** | REST API reference with examples | 10 min |
| **PROJECT_SUMMARY.md** | Architecture and implementation details | 10 min |
| **INDEX.md** | This file - navigation guide | 2 min |

## 🛠️ Utility Scripts

| Script | Purpose |
|--------|---------|
| `verify-setup.bat` | Verify project setup |
| `build-and-run.bat` | Build and run the application |
| `test-ingest.bat` | Test document ingestion |
| `test-query.bat` | Test RAG queries |
| `gradlew.bat` | Gradle wrapper (build tool) |

## 📁 Project Structure

```
camel-docling/
│
├── 📄 Documentation
│   ├── INDEX.md              ← You are here
│   ├── QUICKSTART.md         ← Start here if new
│   ├── README.md             ← Complete documentation
│   ├── API.md                ← API reference
│   └── PROJECT_SUMMARY.md    ← Architecture details
│
├── 🔧 Configuration
│   ├── build.gradle          ← Dependencies and build config
│   ├── settings.gradle       ← Project settings
│   └── src/main/resources/
│       └── application.properties
│
├── 💻 Source Code
│   └── src/main/java/com/example/docling/rag/
│       ├── DoclingRagApplication.java     ← Main application
│       ├── DocumentIngestionRoute.java    ← Ingestion routes
│       ├── RagQueryRoute.java             ← Query routes
│       ├── config/
│       │   └── ChatModelFactory.java      ← LLM configuration
│       ├── service/
│       │   └── VectorStoreService.java    ← Vector storage
│       └── example/
│           └── ProgrammaticUsageExample.java
│
├── 📊 Data
│   ├── data/input/           ← Drop documents here
│   ├── data/output/          ← Processed results
│   ├── data/failed/          ← Failed processing
│   └── data/sample-document.md
│
├── 🧪 Test Scripts
│   ├── verify-setup.bat
│   ├── build-and-run.bat
│   ├── test-ingest.bat
│   └── test-query.bat
│
└── 🏗️ Build System
    ├── gradlew.bat
    └── gradle/wrapper/
```

## 🎯 Common Tasks

### Build the Project
```cmd
gradlew build
```

### Run the Application
```cmd
gradlew run
```
OR
```cmd
build-and-run.bat
```

### Ingest a Document

**Option 1: File System**
```cmd
copy your-document.pdf data\input\
```

**Option 2: HTTP API**
```cmd
curl -X POST http://localhost:8080/api/ingest ^
  -H "Content-Type: application/pdf" ^
  -H "documentName: doc.pdf" ^
  --data-binary @doc.pdf
```

**Option 3: Test Script**
```cmd
test-ingest.bat
```

### Query the System

**Option 1: HTTP API**
```cmd
curl -X POST http://localhost:8080/api/query ^
  -H "Content-Type: application/json" ^
  -d "{\"query\": \"Your question here\"}"
```

**Option 2: Test Script**
```cmd
test-query.bat
```

### Check Health
```cmd
curl http://localhost:8080/api/health
```

## 🔑 Key Components

### 1. Document Ingestion
- **File**: `DocumentIngestionRoute.java`
- **Purpose**: Parse documents with Docling, create embeddings
- **Endpoints**: File watcher, HTTP upload, direct integration

### 2. RAG Query System
- **File**: `RagQueryRoute.java`
- **Purpose**: Handle queries, retrieve context, generate answers
- **Endpoints**: HTTP query, direct query

### 3. Vector Storage
- **File**: `VectorStoreService.java`
- **Purpose**: Manage embeddings and similarity search
- **Features**: Chunking, embedding, retrieval

### 4. Chat Model
- **File**: `ChatModelFactory.java`
- **Purpose**: Configure and provide LLM for answers
- **Supports**: OpenAI, mock model for testing

## 🌟 Features

✅ Apache Camel Docling component for document parsing
✅ Apache Camel LangChain4j-chat for AI integration
✅ Multiple document format support (PDF, DOCX, TXT, MD, HTML)
✅ Automatic text chunking and embedding
✅ Vector similarity search
✅ REST API endpoints
✅ File system watcher
✅ Health monitoring
✅ Comprehensive error handling
✅ Extensive documentation
✅ Test scripts included

## 📚 Learning Resources

### Understanding RAG
1. Read PROJECT_SUMMARY.md section "How It Works"
2. Study the code flow in DocumentIngestionRoute.java
3. Review the query logic in RagQueryRoute.java

### Apache Camel Components
- [Camel Docling Component](https://camel.apache.org/components/latest/docling-component.html)
- [Camel LangChain4j Components](https://camel.apache.org/components/latest/langchain4j-chat-component.html)

### LangChain4j
- [LangChain4j Documentation](https://docs.langchain4j.dev/)

## 🐛 Troubleshooting

| Issue | Solution | Details |
|-------|----------|---------|
| Port 8080 in use | Change port in application.properties | API.md |
| Java not found | Install Java 17+ | QUICKSTART.md |
| Build fails | Check Java version | QUICKSTART.md |
| No responses | Ingest documents first | README.md |
| Parse errors | Check document format | README.md |

## 🔧 Configuration

### Essential Settings
- **Port**: `application.properties` → `camel.component.jetty.port`
- **Chunk Size**: `application.properties` → `document.chunk.size`
- **OpenAI Key**: Environment variable `OPENAI_API_KEY`

### Optional Enhancements
- Switch vector store (edit VectorStoreService.java)
- Change LLM model (edit ChatModelFactory.java)
- Adjust chunk overlap (edit application.properties)
- Add authentication (add security to routes)

## 🚀 Production Checklist

Before deploying to production:
- [ ] Set OPENAI_API_KEY environment variable
- [ ] Replace in-memory store with persistent storage
- [ ] Add API authentication
- [ ] Implement rate limiting
- [ ] Set up monitoring and logging
- [ ] Configure HTTPS
- [ ] Review and adjust chunk sizes
- [ ] Test with production document types
- [ ] Set up backup and recovery
- [ ] Configure resource limits

## 💡 Next Steps

### Immediate (5 minutes)
1. Run `verify-setup.bat`
2. Run `build-and-run.bat`
3. Test with `test-ingest.bat` and `test-query.bat`

### Short Term (1 hour)
1. Add your own documents to `data/input/`
2. Experiment with different queries
3. Review the source code
4. Try the programmatic example

### Long Term (Ongoing)
1. Integrate into your application
2. Customize for your use case
3. Add authentication and security
4. Deploy to production
5. Scale and optimize

## 📞 Support

- **Documentation**: You're reading it! Check the files listed above
- **Code Examples**: See `example/ProgrammaticUsageExample.java`
- **API Reference**: See `API.md`
- **Architecture**: See `PROJECT_SUMMARY.md`

## 📝 Version Information

- **Project Version**: 1.0.0
- **Camel Version**: 4.9.0
- **LangChain4j Version**: 0.35.0
- **Java Version**: 17+

## 🎉 Success Indicators

You'll know the system is working when:
1. ✅ Verification script passes all checks
2. ✅ Application starts without errors
3. ✅ Health endpoint returns status "UP"
4. ✅ Test ingestion completes successfully
5. ✅ Test queries return relevant answers

---

## 🏁 Ready to Start?

Choose your path above and dive in! The quickest way to see results:

```cmd
verify-setup.bat && build-and-run.bat
```

Then in another terminal:
```cmd
test-ingest.bat && test-query.bat
```

Happy coding! 🚀
