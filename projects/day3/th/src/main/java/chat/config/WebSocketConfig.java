package chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket configuration for the chat application.
 *
 * <p>Configures STOMP over WebSocket with SockJS fallback so that browsers
 * without native WebSocket support can still connect via long-polling or
 * other transports provided by SockJS.
 *
 * <p>Message flow:
 * <ol>
 *   <li>Clients connect to the {@code /chat-ws} endpoint.</li>
 *   <li>Client-to-server messages are sent to destinations prefixed with {@code /app},
 *       which routes them to {@code @MessageMapping} methods in controllers.</li>
 *   <li>The simple in-memory broker forwards messages from the {@code /topic}
 *       prefix to all subscribed clients.</li>
 * </ol>
 *
 * <p>Branch: feature/websocket-core
 *
 * @see chat.controller.ChatController
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configures the message broker used to route messages to subscribers.
     *
     * <p>A simple in-memory broker is enabled for the {@code /topic} destination
     * prefix. The {@code /app} prefix is reserved for messages that must be
     * processed by a {@code @MessageMapping} controller method before being
     * forwarded to the broker.
     *
     * @param registry the registry used to configure broker options
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Enable the built-in simple broker for topic-based subscriptions.
        // Clients subscribe to destinations like /topic/public to receive
        // broadcast messages without any server-side routing logic.
        registry.enableSimpleBroker("/topic");

        // Messages sent to /app/... are routed to @MessageMapping methods
        // before being forwarded to the broker.
        registry.setApplicationDestinationPrefixes("/app");
    }

    /**
     * Registers the STOMP WebSocket endpoint that clients connect to.
     *
     * <p>SockJS is enabled as a fallback transport so clients on networks that
     * block WebSocket connections (e.g., certain proxies) can still communicate
     * via HTTP long-polling or server-sent events.
     *
     * @param registry the registry used to register STOMP endpoints
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // /chat-ws is the handshake URL. withSockJS() enables the SockJS
        // protocol for clients that cannot use a raw WebSocket connection.
        registry.addEndpoint("/chat-ws")
                .withSockJS();
    }
}
