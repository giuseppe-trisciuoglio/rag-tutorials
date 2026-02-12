package com.github.rag.tutorials.chatbot.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ChatBotHistoryResponse {
    private String memoryId;
    private List<ChatBotMessage> messages;
}
