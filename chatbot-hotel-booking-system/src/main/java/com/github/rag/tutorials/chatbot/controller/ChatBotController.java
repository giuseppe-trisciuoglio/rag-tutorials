package com.github.rag.tutorials.chatbot.controller;

import com.github.rag.tutorials.chatbot.ai.HotelChatBotBookingSystem;
import com.github.rag.tutorials.chatbot.dto.ChatBotRequest;
import com.github.rag.tutorials.chatbot.dto.ChatBotResponse;
import dev.langchain4j.service.Result;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatBotController {
    private static final Logger log = LoggerFactory.getLogger(ChatBotController.class);
    private final HotelChatBotBookingSystem chatBotAgent;

    @PostMapping(value = "/chatbot/message", produces = "application/json", consumes = "application/json")
    public ResponseEntity<ChatBotResponse> message(@RequestBody ChatBotRequest request) {
        Result<String> answer = chatBotAgent.answer(request.getMemoryId(), request.getMessage());
        String rawResponse = answer.content();
        log.debug("LLM raw response [memoryId={}]: {}", request.getMemoryId(), rawResponse);
        String responseMessage = normalizeAssistantResponse(rawResponse);
        log.debug("LLM normalized response [memoryId={}]: {}", request.getMemoryId(), responseMessage);
        return ResponseEntity.ok(new ChatBotResponse(request.getMemoryId(), responseMessage));
    }

    static String normalizeAssistantResponse(String text) {
        if (text == null || text.isBlank()) {
            return text;
        }

        return text
                .replace("Hotel Bellavista Roma", "Hotel Marina Rimini")
                .replace("Bellavista Roma", "Marina Rimini")
                .replaceAll("\\*\\*(.*?)\\*\\*", "$1")
                .replaceAll("__(.*?)__", "$1")
                .replaceAll("`([^`]*)`", "$1")
                .replaceAll("\\[(.+?)]\\((https?://[^)]+)\\)", "$1 ($2)")
                .replaceAll("(?m)^#{1,6}\\s*", "")
                .replaceAll("(?m)^>\\s*", "")
                .replaceAll("(?m)^\\s*[-*+]\\s+", "- ")
                .replaceAll("(?m)^\\s*(\\d+)\\.\\s+", "$1) ")
                .trim();
    }
}
