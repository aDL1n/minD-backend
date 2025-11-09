package dev.adlin.mind.service;

import dev.adlin.mind.ChatMessage;
import dev.adlin.mind.repository.ChatHistoryRepository;
import dev.adlin.mind.repository.entity.ChatMessageEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ChatService {

    private final ChatHistoryRepository chatHistory;

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public List<ChatMessage> getMessages(Integer limit) {
        if (limit == null) throw new IllegalArgumentException("limit is null");

        return this.chatHistory.findLastMessages(limit)
                .stream().map(ChatMessageEntity::toChatMessage).collect(Collectors.toList());
    }

    public List<ChatMessage> getPrevMessages(Long beforeId, Integer limit) {
        if (limit == null) throw new IllegalArgumentException("limit is null");

        return this.chatHistory.findPrevMessages(beforeId, limit)
                .reversed().stream().map(ChatMessageEntity::toChatMessage).collect(Collectors.toList());
    }

    public ChatMessage receiveMessage(ChatMessage message) {
        if (message.id() != null) throw new IllegalArgumentException("message id should be null");
        if (message.timestamp() != null) throw new IllegalArgumentException("message timestamp should be null");

        ChatMessageEntity entity = new ChatMessageEntity(
                null,
                message.message(),
                System.currentTimeMillis()
        );

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(entity);
            } catch (IOException e) {
                emitter.complete();
                emitters.remove(emitter);
            }
        }

        return this.toDomain(this.chatHistory.save(entity));
    }

    public SseEmitter registerEmitter() {
        SseEmitter emitter = new SseEmitter(60000L);
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));

        return emitter;
    }

    public int getOnlineCount() {
        return this.emitters.size();
    }

    private ChatMessage toDomain(ChatMessageEntity entity) {
        return new ChatMessage(entity.getId(), entity.getMessage(), entity.getTimestamp());
    }
}
