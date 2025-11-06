package dev.adlin.mind.service;

import dev.adlin.mind.ChatMessage;
import dev.adlin.mind.repository.ChatHistoryRepository;
import dev.adlin.mind.repository.entity.ChatMessageEntity;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final ChatHistoryRepository chatHistory;

    public ChatService(ChatHistoryRepository chatHistoryRepository) {
        this.chatHistory = chatHistoryRepository;
    }

    public List<ChatMessage> getMessages(Long start, Integer limit) {
        if (start == null) throw new IllegalArgumentException("start is null");
        if (limit == null) throw new IllegalArgumentException("limit is null");

        return this.chatHistory.findChatMessageEntitiesById(start, Limit.of(limit))
                .stream()
                .map(ChatMessageEntity::toChatMessage)
                .collect(Collectors.toList());
    }

    public ChatMessage receiveMessage(ChatMessage message) {
        if (message.id() != null) throw new IllegalArgumentException("message id should be null");
        if (message.timestamp() != null) throw new IllegalArgumentException("message timestamp should be null");

        ChatMessageEntity entity = new ChatMessageEntity(
                null,
                message.message(),
                System.currentTimeMillis()
        );

        return this.toDomain(this.chatHistory.save(entity));
    }

    private ChatMessage toDomain(ChatMessageEntity entity) {
        return new ChatMessage(entity.getId(), entity.getMessage(), entity.getTimestamp());
    }
}
