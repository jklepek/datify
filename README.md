# DATIFY - AI-powered Document Q&A System

DATIFY is an AI-powered document processing and question-answering system that allows users to upload documents (PDF, TXT) and ask questions about their content using OpenAI's language models.

## Features

- 📄 **Document Upload**: Support for PDF and TXT files up to 10MB
- 🔍 **Text Extraction**: Automatic text extraction using Apache Tika
- 🧠 **Text Similarity**: Simple word-frequency based embeddings for document similarity
- 💬 **Question Answering**: Ask questions about uploaded documents in Czech language using Google Gemini
- 🚀 **RAG Pattern**: Retrieval-Augmented Generation for accurate responses
- 💾 **In-Memory Storage**: Fast in-memory vector storage for MVP
- 🎨 **Modern UI**: React frontend with Tailwind CSS

## Tech Stack

### Backend
- Java 21
- Spring Boot 3.5.5
- Spring Data JPA
- H2 Database (in-memory)
- Apache Tika (text extraction)
- Google Gemini API integration
- Gradle build system

### Frontend
- React 18
- Vite (build tool)
- Tailwind CSS
- Axios for API calls
- React Dropzone for file uploads

## Prerequisites

- Java 17+ (Java 21 recommended)
- Node.js 16+ and npm
- Google Gemini API key

## Setup Instructions

### 1. Clone the repository

```bash
git clone <repository-url>
cd datify
```

### 2. Backend Setup

#### Configure Google Gemini API Key

Set your Google Gemini API key as an environment variable:

```bash
export GEMINI_API_KEY=your_gemini_api_key_here
```

Or create an `application-local.properties` file in `src/main/resources/`:

```properties
gemini.api.key=your_gemini_api_key_here
```

#### Build and run the backend

```bash
# Build the application
./gradlew build

# Run the application
./gradlew bootRun
```

The backend will start on `http://localhost:8080`

### 3. Frontend Setup

Navigate to the frontend directory and install dependencies:

```bash
cd frontend
npm install
```

Start the Vite development server:

```bash
npm run dev
```

The frontend will start on `http://localhost:3000` and proxy API requests to the backend.

## API Endpoints

### Document Management

- `POST /api/documents/upload` - Upload a document
  - Body: `multipart/form-data` with `file` field
  - Response: Document metadata with ID

- `GET /api/documents` - List all uploaded documents
  - Response: Array of document metadata

- `GET /api/documents/{id}` - Get specific document details
  - Response: Document metadata

### Question Answering

- `POST /api/documents/{id}/ask` - Ask a question about a document
  - Body: `{"question": "Your question here"}`
  - Response: `{"answer": "AI response", "question": "...", "documentId": 1, "documentFilename": "..."}`

## Usage

1. **Upload a Document**
   - Visit `http://localhost:3000`
   - Drag and drop a PDF or TXT file, or click to select
   - Wait for the upload and processing to complete

2. **Ask Questions**
   - Select an uploaded document from the list
   - Type your question in Czech in the chat interface
   - Get AI-powered answers based on the document content

## Project Structure

```
datify/
├── src/main/java/com/klepek/datify/
│   ├── DatifyApplication.java          # Main Spring Boot application
│   ├── entity/
│   │   └── Document.java               # Document entity with embeddings
│   ├── dto/                           # Data Transfer Objects
│   │   ├── DocumentResponse.java
│   │   ├── QuestionRequest.java
│   │   └── AnswerResponse.java
│   ├── repository/
│   │   └── DocumentRepository.java     # JPA repository
│   ├── service/
│   │   ├── DocumentService.java        # Business logic
│   │   ├── OpenAIService.java         # OpenAI API integration
│   │   └── VectorService.java         # Vector similarity calculations
│   └── controller/
│       └── DocumentController.java     # REST API endpoints
├── src/main/resources/
│   └── application.properties          # Configuration
├── frontend/
│   ├── public/
│   ├── src/
│   │   ├── components/                # React components
│   │   ├── services/                  # API service layer
│   │   ├── App.js                     # Main React component
│   │   └── index.js                   # React entry point
│   ├── package.json
│   └── tailwind.config.js
└── build.gradle                       # Gradle build configuration
```

## Architecture

### Document Processing Flow

1. **Upload**: User uploads PDF/TXT file via React frontend
2. **Text Extraction**: Apache Tika extracts text content
3. **Embedding Creation**: OpenAI API creates vector embeddings
4. **Storage**: Document and embeddings stored in H2 database and in-memory vector store

### Question Answering Flow

1. **Question Input**: User asks question about specific document
2. **Embedding**: Question converted to vector embedding
3. **Similarity Search**: Find relevant document content using cosine similarity
4. **RAG**: Send relevant context + question to OpenAI for answer generation
5. **Response**: AI-generated answer returned in Czech

## Configuration

Key configuration options in `application.properties`:

```properties
# OpenAI API
openai.api.key=${OPENAI_API_KEY:your-openai-api-key-here}

# Database (H2 in-memory)
spring.datasource.url=jdbc:h2:mem:datify
spring.h2.console.enabled=true

# File Upload
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Server
server.port=8080
```

## Development

### Running Tests

```bash
# Backend tests
./gradlew test

# Frontend build test
cd frontend
npm run build
```

### Building for Production

```bash
# Build backend
./gradlew build

# Build frontend
cd frontend
npm run build
```

## Limitations (MVP)

- In-memory storage (data lost on restart)
- Single document context (no cross-document queries)
- Basic text chunking strategy
- Czech language responses only
- No user authentication
- No document versioning

## Future Enhancements

- Persistent vector database (e.g., Pinecone, Weaviate)
- Multi-document query support
- Advanced text chunking and preprocessing
- User authentication and document ownership
- Document versioning and updates
- Support for more file formats (DOCX, etc.)
- Multilingual support
- Advanced search and filtering

## License

This project is for interview demonstration purposes.

## Contact

For questions about this implementation, please contact the development team.