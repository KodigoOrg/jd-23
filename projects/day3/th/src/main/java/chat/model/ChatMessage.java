package chat.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing a chat message persisted in the database.
 *
 * <p>Each message has a sender, text content, a UTC timestamp, and a type that
 * distinguishes regular chat messages from join/leave system events.
 *
 * <p>Branch: feature/websocket-core
 *
 * @see chat.repository.ChatMessageRepository
 * @see chat.service.MessageService
 */
@Entity
@Table(name = "chat_messages")
public class ChatMessage {

    /**
     * Message type used to differentiate user messages from system events.
     */
    public enum Type {
        /** A regular text message sent by a user. */
        CHAT,
        /** Broadcast when a user joins the chat room. */
        JOIN,
        /** Broadcast when a user leaves the chat room. */
        LEAVE
    }

    /** Auto-generated primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Username of the person who sent the message. */
    @Column(nullable = false, length = 64)
    private String sender;

    /** Text body of the message (null for JOIN/LEAVE events). */
    @Column(length = 2048)
    private String content;

    /** UTC date and time when this message was created. */
    @Column(nullable = false)
    private LocalDateTime timestamp;

    /** Discriminates between CHAT, JOIN, and LEAVE messages. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 8)
    private Type type;

    // --- Constructors ---

    /** Required by JPA. */
    protected ChatMessage() {}

    /**
     * Full constructor used by the service layer.
     *
     * @param sender    username of the author
     * @param content   message text (may be null for JOIN/LEAVE)
     * @param timestamp creation time in UTC
     * @param type      one of CHAT, JOIN, or LEAVE
     */
    public ChatMessage(String sender, String content, LocalDateTime timestamp, Type type) {
        this.sender = sender;
        this.content = content;
        this.timestamp = timestamp;
        this.type = type;
    }

    // --- Getters and setters ---

    /**
     * Returns the auto-generated database identifier.
     *
     * @return message id, or {@code null} if not yet persisted
     */
    public Long getId() { return id; }

    /**
     * Returns the username of the message author.
     *
     * @return sender name
     */
    public String getSender() { return sender; }

    /**
     * Sets the sender username.
     *
     * @param sender non-null username
     */
    public void setSender(String sender) { this.sender = sender; }

    /**
     * Returns the text content of the message.
     *
     * @return message body, may be {@code null} for JOIN/LEAVE events
     */
    public String getContent() { return content; }

    /**
     * Sets the message body.
     *
     * @param content text content
     */
    public void setContent(String content) { this.content = content; }

    /**
     * Returns the UTC timestamp of when this message was created.
     *
     * @return creation timestamp
     */
    public LocalDateTime getTimestamp() { return timestamp; }

    /**
     * Sets the creation timestamp.
     *
     * @param timestamp UTC date-time
     */
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    /**
     * Returns the message type (CHAT, JOIN, or LEAVE).
     *
     * @return message type
     */
    public Type getType() { return type; }

    /**
     * Sets the message type.
     *
     * @param type one of CHAT, JOIN, or LEAVE
     */
    public void setType(Type type) { this.type = type; }
}
