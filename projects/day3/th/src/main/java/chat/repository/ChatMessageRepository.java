package chat.repository;

import chat.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link ChatMessage} entities.
 *
 * <p>Provides standard CRUD operations inherited from {@link JpaRepository}
 * plus a custom query to retrieve the most recent messages for the initial
 * chat page load.
 *
 * <p>Branch: feature/websocket-core
 *
 * @see chat.service.MessageService
 */
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    /**
     * Returns the 50 most recent chat messages ordered from newest to oldest.
     *
     * <p>Spring Data derives this query from the method name:
     * {@code findTop50} limits results to 50 rows, {@code OrderByTimestampDesc}
     * sorts by the {@code timestamp} column in descending order so that the
     * latest messages come first. The caller is responsible for reversing the
     * list before rendering if chronological order is desired.
     *
     * @return at most 50 {@link ChatMessage} instances, newest first
     */
    List<ChatMessage> findTop50ByOrderByTimestampDesc();
}
