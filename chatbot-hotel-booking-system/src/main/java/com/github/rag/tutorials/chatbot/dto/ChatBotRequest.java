package com.github.rag.tutorials.chatbot.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatBotRequest {
    private String memoryId;
    private String message;
}
