package dev.adlin.mind.repository;

import dev.adlin.mind.repository.entity.ChatMessageEntity;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatHistoryRepository extends JpaRepository<ChatMessageEntity, Long> {

    List<ChatMessageEntity> findChatMessageEntitiesById(Long id, Limit limit);
}
