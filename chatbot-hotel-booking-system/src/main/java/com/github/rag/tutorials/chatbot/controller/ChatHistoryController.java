package com.github.rag.tutorials.chatbot.controller;

import com.github.rag.tutorials.chatbot.dto.ChatBotHistoryResponse;
import com.github.rag.tutorials.chatbot.dto.ChatBotMessage;
import com.github.rag.tutorials.chatbot.model.Message;
import com.github.rag.tutorials.chatbot.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class ChatHistoryController {
    private final MessageRepository messageRepository;

    @GetMapping(value = "/message/history", produces = "application/json")
    public ResponseEntity<ChatBotHistoryResponse> message(@RequestParam("memoryId") String memoryId) {
        Optional<Message> messages = messageRepository.findByMemoryId(memoryId);
        if (messages.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        ChatBotHistoryResponse response = new ChatBotHistoryResponse();
        response.setMemoryId(memoryId);
        
        messages.ifPresent(message -> {
            List<ChatBotMessage> chatBotMessages = ChatBotMessage.fromJson(memoryId, message.getMessages());
            response.setMessages(chatBotMessages);
        });

        return ResponseEntity.ok(response);
    }
}
