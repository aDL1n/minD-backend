package dev.adlin.mind.repository.entity;

import dev.adlin.mind.ChatMessage;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "messages")
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageEntity {

    @Id
    @Column(name = "Id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message")
    private String message;

    @Column(name = "timestamp")
    private Long timestamp;

    public static ChatMessage toChatMessage(ChatMessageEntity entity) {
        return new ChatMessage(entity.getId(), entity.getMessage(), entity.getTimestamp());
    }
}
