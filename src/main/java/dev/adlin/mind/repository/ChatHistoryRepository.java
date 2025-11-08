package dev.adlin.mind.repository;

import dev.adlin.mind.repository.entity.ChatMessageEntity;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatHistoryRepository extends JpaRepository<ChatMessageEntity, Long> {

    @Query(
            value = """
            SELECT *
            FROM (
                SELECT *
                FROM messages
                ORDER BY timestamp DESC
                LIMIT :limit
            ) AS last_messages
            ORDER BY timestamp ASC
            """,
            nativeQuery = true
    )
    List<ChatMessageEntity> findLastMessages(@Param("limit") int limit);

    @Query(value = "SELECT * FROM messages WHERE id <= :beforeId ORDER BY id DESC LIMIT :limit", nativeQuery = true)
    List<ChatMessageEntity> findPrevMessages(@Param("beforeId") long beforeId, @Param("limit") int limit);
}
