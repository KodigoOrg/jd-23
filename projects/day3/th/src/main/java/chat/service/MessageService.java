package chat.service;

import chat.model.ChatMessage;

import java.util.List;

/**
 * Service interface for chat message persistence and retrieval.
 *
 * <p>Abstracts storage details from the WebSocket controller layer.
 * The default implementation uses Spring Data JPA backed by an H2 database.
 *
 * <p>Branch: feature/websocket-core
 *
 * @see MessageServiceImpl
 * @see chat.controller.ChatController
 */
public interface MessageService {

    /**
     * Persists a {@link ChatMessage} to the database.
     *
     * <p>The timestamp is set by the implementation before saving if it is
     * not already present on the entity.
     *
     * @param message the message to save; must not be {@code null}
     * @return the saved entity with its generated {@code id} populated
     */
    ChatMessage saveMessage(ChatMessage message);

    /**
     * Retrieves the most recent messages up to the requested limit.
     *
     * <p>Results are returned in ascending (chronological) order so that
     * callers can render them top-to-bottom without extra sorting.
     *
     * @param limit maximum number of messages to return (typically 50)
     * @return list of messages in chronological order, never {@code null}
     */
    List<ChatMessage> getLastMessages(int limit);
}
