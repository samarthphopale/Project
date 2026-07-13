package com.exim.v1.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/eximAI")
public class TradeAIController {

    @Value("${spring.ai.service.url}")
    private String aiServiceUrl;

    @Value("${spring.ai.service.chatEndpoint}")
    private String chatEndpoint;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/chat")
    public String askAI(@RequestBody String prompt) {
        
        // FIX: Replaced fromHttpUrl with fromUriString to fix the "method undefined" compiler error
        String targetUrl = UriComponentsBuilder.fromUriString(aiServiceUrl)
                .path(chatEndpoint) 
                .queryParam("prompt", prompt)
                .toUriString();

        return restTemplate.postForObject(targetUrl, null, String.class);
    }
}
