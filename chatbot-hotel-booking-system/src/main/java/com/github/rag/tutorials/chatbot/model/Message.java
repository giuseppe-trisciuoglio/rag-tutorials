package com.github.rag.tutorials.chatbot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity(name = "message")
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
public class Message {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;
    @Column(name = "memory_id", nullable = false, unique = true)
    private String memoryId;
    @Column(columnDefinition = "TEXT", nullable = false)
    private String messages;

    public Message(String memoryId, String messages) {
        this.memoryId = memoryId;
        this.messages = messages;
    }
}
