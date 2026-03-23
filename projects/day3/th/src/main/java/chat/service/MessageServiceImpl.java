package chat.service;

import chat.model.ChatMessage;
import chat.repository.ChatMessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Default implementation of {@link MessageService} using Spring Data JPA.
 *
 * <p>Messages are stored in an H2 in-memory database. The repository query
 * returns rows newest-first; this class reverses that order so callers
 * always receive messages in chronological (oldest-first) sequence.
 *
 * <p>Branch: feature/websocket-core
 *
 * @see ChatMessageRepository
 */
@Service
@Transactional
public class MessageServiceImpl implements MessageService {

    private final ChatMessageRepository repository;

    /**
     * Constructs the service with its required repository dependency.
     *
     * @param repository JPA repository for {@link ChatMessage} entities
     */
    public MessageServiceImpl(ChatMessageRepository repository) {
        this.repository = repository;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Assigns the current UTC time as the timestamp when the message does
     * not already carry one, ensuring every row has a valid timestamp before
     * being handed to JPA.
     *
     * @param message the message to save; must not be {@code null}
     * @return the saved entity with its generated {@code id} populated
     */
    @Override
    public ChatMessage saveMessage(ChatMessage message) {
        // Stamp the creation time here rather than in the controller so that
        // the timestamp reflects when the record hits the database, not when
        // the WebSocket frame arrived.
        if (message.getTimestamp() == null) {
            message.setTimestamp(LocalDateTime.now());
        }
        return repository.save(message);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The underlying query fetches at most 50 rows ordered newest-first.
     * We reverse the list in memory so the caller always gets oldest-first
     * order, which matches how a chat history is displayed (top = oldest).
     * The {@code limit} parameter is accepted for API compatibility but the
     * repository query already caps results at 50.
     *
     * @param limit maximum number of messages to return
     * @return messages in ascending (chronological) order
     */
    @Override
    @Transactional(readOnly = true)
    public List<ChatMessage> getLastMessages(int limit) {
        List<ChatMessage> messages = repository.findTop50ByOrderByTimestampDesc();

        // The query returns newest-first; reverse so the UI renders
        // messages from oldest (top) to newest (bottom).
        Collections.reverse(messages);
        return messages;
    }
}
