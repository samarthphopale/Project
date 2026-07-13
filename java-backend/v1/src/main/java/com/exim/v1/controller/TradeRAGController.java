package com.exim.v1.controller;

import com.exim.v1.service.TradeRAGService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;       
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/eximAI")
public class TradeRAGController {

    private final TradeRAGService tradeRAGService;

    // Standard constructor-based dependency injection
    public TradeRAGController(TradeRAGService tradeRAGService) {
        this.tradeRAGService = tradeRAGService;
    }

    /**
     * 1. INGESTION ENDPOINT
     * Consumes raw text documents (like EXIM policy rules) and saves them as vectors in ChromaDB.
     */
    @PostMapping("/ingest")
    public ResponseEntity<String> ingestDocument(@RequestBody String documentText) {
        if (documentText == null || documentText.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("❌ Error: Document text payload cannot be empty.");
        }
        
        tradeRAGService.ingestEximDocument(documentText);
        return ResponseEntity.ok("✅ Success: EXIM Document chunks successfully vectorized and saved into ChromaDB!");
    }


/**
     * 1b. INGESTION ENDPOINT (PDF)
     * Consumes a PDF file, extracts its text, and saves it as vectors in ChromaDB.
     */
    @PostMapping(value = "/ingest-pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> ingestPdfDocument(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("❌ Error: PDF file cannot be empty.");
        }

        // Validate that the uploaded file is actually a PDF
        if (!"application/pdf".equals(file.getContentType())) {
            return ResponseEntity.badRequest().body("❌ Error: Only PDF files are supported at this endpoint.");
        }

        try {
            tradeRAGService.ingestEximPdf(file);
            return ResponseEntity.ok("✅ Success: EXIM PDF text successfully extracted, vectorized, and saved into ChromaDB!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Error processing PDF: " + e.getMessage());
        }
    }

    /**
     * 2. RAG CHAT ENDPOINT
     * Extracts relevant context from ChromaDB based on the user's question, then asks Ollama.
     */
    @PostMapping("/rag-chat")
    public ResponseEntity<String> chatWithContext(@RequestBody String userPrompt) {
        if (userPrompt == null || userPrompt.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("❌ Error: Prompt text cannot be empty.");
        }

        String aiResponse = tradeRAGService.queryWithContext(userPrompt);
        return ResponseEntity.ok(aiResponse);
    }
}
