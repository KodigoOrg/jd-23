package chat.controller;

import chat.model.ChatMessage;
import chat.service.MessageService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Controller handling both STOMP WebSocket messages and a REST endpoint
 * for loading chat history.
 *
 * <p>STOMP flow:
 * <ol>
 *   <li>Client sends a frame to {@code /app/chat.sendMessage} or
 *       {@code /app/chat.addUser}.</li>
 *   <li>Spring routes the frame to the corresponding {@code @MessageMapping} method.</li>
 *   <li>The method persists the message and returns it; Spring then forwards
 *       the return value to all subscribers of {@code /topic/public}.</li>
 * </ol>
 *
 * <p>The REST endpoint {@code GET /api/messages} is used on initial page load
 * to hydrate the chat view with recent history before the WebSocket connects.
 *
 * <p>Branch: feature/websocket-core (REST endpoint added during thymeleaf-ui merge)
 *
 * @see chat.config.WebSocketConfig
 * @see MessageService
 */
@Controller
public class ChatController {

    private final MessageService messageService;

    /**
     * Constructs the controller with its message persistence dependency.
     *
     * @param messageService service used to save and retrieve chat messages
     */
    public ChatController(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * Handles a regular chat message sent by a connected user.
     *
     * <p>STOMP destination: {@code /app/chat.sendMessage}<br>
     * Broadcast destination: {@code /topic/public}
     *
     * <p>The message is persisted before being forwarded to the broker so
     * that even if the broadcast fails, the record is not lost.
     *
     * @param message the incoming chat message deserialized from the STOMP frame body
     * @return the saved {@link ChatMessage} that will be broadcast to all subscribers
     */
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage message) {
        // Persist the message so it survives page refreshes and new joiners
        // can load recent history via the REST endpoint.
        return messageService.saveMessage(message);
    }

    /**
     * Handles a user join event when a new participant enters the chat room.
     *
     * <p>STOMP destination: {@code /app/chat.addUser}<br>
     * Broadcast destination: {@code /topic/public}
     *
     * <p>The client sets the message type to JOIN and the sender to the
     * username chosen on the login page. This event is persisted and
     * broadcast so all connected clients can display the join notification.
     *
     * @param message the JOIN message carrying the new user's name
     * @return the saved {@link ChatMessage} broadcast to all subscribers
     */
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage message) {
        // Persist JOIN events so the history endpoint also shows who joined.
        return messageService.saveMessage(message);
    }

    /**
     * REST endpoint that returns the last 50 chat messages in chronological order.
     *
     * <p>Called by the chat page JavaScript on load to populate the message list
     * before the WebSocket connection is established, so there is no blank screen
     * while STOMP handshake completes.
     *
     * @return list of up to 50 {@link ChatMessage} objects serialized as JSON
     */
    @GetMapping("/api/messages")
    @ResponseBody
    public List<ChatMessage> getMessages() {
        return messageService.getLastMessages(50);
    }
}
