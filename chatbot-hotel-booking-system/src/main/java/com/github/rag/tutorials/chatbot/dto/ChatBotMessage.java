package com.github.rag.tutorials.chatbot.dto;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

import static dev.langchain4j.data.message.ChatMessageDeserializer.messagesFromJson;

@Getter
@Setter
@AllArgsConstructor
public class ChatBotMessage {
    private String memoryId;
    private String message;
    private boolean isUser;

    public static List<ChatBotMessage> fromJson(String memoryId, String messages) {
        final List<ChatBotMessage> chatBotMessages = new LinkedList<>();
        List<ChatMessage> chatMessages = messagesFromJson(messages);
        chatMessages.forEach(chatMessage -> chatBotMessages
                .add(new ChatBotMessage(memoryId, chatMessage.text(), chatMessage.type().equals(ChatMessageType.USER))));
        return chatBotMessages;
    }
}
