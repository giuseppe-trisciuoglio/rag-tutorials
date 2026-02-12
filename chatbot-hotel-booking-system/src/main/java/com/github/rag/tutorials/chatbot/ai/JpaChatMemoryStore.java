package com.github.rag.tutorials.chatbot.ai;

import com.github.rag.tutorials.chatbot.model.Message;
import com.github.rag.tutorials.chatbot.repository.MessageRepository;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static dev.langchain4j.data.message.ChatMessageDeserializer.messagesFromJson;
import static dev.langchain4j.data.message.ChatMessageSerializer.messagesToJson;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class JpaChatMemoryStore implements ChatMemoryStore {
    private final MessageRepository messageRepository;

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        return messagesFromJson(messageRepository.findByMemoryId(memoryId.toString())
                .map(Message::getMessages)
                .orElse("[]"));
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> list) {
        Optional<Message> message = messageRepository.findByMemoryId(memoryId.toString());
        if (message.isEmpty()) {
            messageRepository.save(new Message(memoryId.toString(), messagesToJson(list)));
        } else {
            message.ifPresent(m -> {
                m.setMessages(messagesToJson(list));
                messageRepository.save(m);
            });
        }
    }

    @Override
    public void deleteMessages(Object memoryId) {
        messageRepository.deleteByMemoryId(memoryId.toString());
    }
}
