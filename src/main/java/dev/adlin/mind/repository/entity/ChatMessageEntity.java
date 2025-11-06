package dev.adlin.mind.repository.entity;

import dev.adlin.mind.ChatMessage;
import jakarta.persistence.*;

@Table(name = "messages")
@Entity
public class ChatMessageEntity {

    @Id
    @Column(name = "Id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message")
    private String message;

    @Column(name = "timestamp")
    private Long timestamp;

    public ChatMessageEntity(Long id, String message, long timestamp) {
        this.id = id;
        this.message = message;
        this.timestamp = timestamp;
    }

    public ChatMessageEntity() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public static ChatMessage toChatMessage(ChatMessageEntity entity) {
        return new ChatMessage(entity.getId(), entity.getMessage(), entity.getTimestamp());
    }
}
