package com.exim.v1.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.web.multipart.MultipartFile;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import java.io.IOException;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TradeRAGService {

    private final VectorStore vectorStore;
    private final RestTemplate restTemplate = new RestTemplate();

    private final TokenTextSplitter textSplitter = new TokenTextSplitter(800, 350, 10, 10000, true);

    @Value("${spring.ai.service.url}")
    private String aiServiceUrl;

    @Value("${spring.ai.service.chatEndpoint}")
    private String chatEndpoint;

    public TradeRAGService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    // 1. INGESTION: Split document text into vector math chunks and save to
    // ChromaDB
    public void ingestEximDocument(String documentText) {
        Document doc = new Document(documentText);
        // Spring AI automatically calls Ollama to create embeddings and saves them to
        // Chroma
        List<Document> splitDocuments = textSplitter.apply(List.of(doc));
        vectorStore.add(splitDocuments);
    }

    public void ingestEximPdf(MultipartFile file) throws IOException {
        String extractedText;
        
        // Pass file.getBytes() to match the Loader.loadPDF(byte[]) signature
        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            extractedText = pdfStripper.getText(document);
        }

        if (extractedText == null || extractedText.trim().isEmpty()) {
            throw new IllegalArgumentException("The PDF file contains no readable text.");
        }

        // Pass the extracted text to your vector DB logic on line 37
        this.ingestEximDocument(extractedText);
    }

    // 2. RETRIEVAL & GENERATION: Extract matching context and route it to your
    // Python container
    public String queryWithContext(String userPrompt) {
        // Query ChromaDB for the top 3 most relevant context document blocks matching
        // the prompt
        List<Document> similarDocuments = vectorStore.similaritySearch(userPrompt);

        String extractedContext = similarDocuments.stream()

                .map(Document::getContent)

                .collect(Collectors.joining("\n"));

        // Build an enriched, reinforced prompt payload structure
        String enrichedPrompt = "Context information from EXIM Documents:\n" + extractedContext +
                "\n\nBased on the context above, answer this question: " + userPrompt;

        // Route the clean enriched context string straight to your existing Python
        // pipeline container
        String targetUrl = UriComponentsBuilder.fromUriString(aiServiceUrl)
                .path(chatEndpoint)
                .queryParam("prompt", enrichedPrompt)
                .toUriString();

        return restTemplate.postForObject(targetUrl, null, String.class);
    }
}
