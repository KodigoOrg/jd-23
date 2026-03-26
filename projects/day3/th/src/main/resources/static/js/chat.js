/*
 * chat.js — WebSocket / STOMP chat client.
 *
 * Depends on:
 *   - window.CURRENT_USER  (injected by Thymeleaf in chat.html)
 *   - sockjs-client        (loaded before this script)
 *   - stompjs              (loaded before this script)
 */

/* ── DOM references ───────────────────────────────────────────────────────── */
const messageArea  = document.getElementById('messageArea');
const messageInput = document.getElementById('messageInput');
const sendBtn      = document.getElementById('sendBtn');

let stompClient = null;

/* ── Helper: format ISO timestamp to HH:MM ───────────────────────────────── */
function formatTime(isoString) {
    if (!isoString) return '';
    const d = new Date(isoString);
    return d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
}

/* ── Render a single message into the chat area ──────────────────────────── */
function appendMessage(msg) {
    if (msg.type === 'CHAT') {
        const isOwn  = msg.sender === window.CURRENT_USER;
        const bubble = document.createElement('div');
        bubble.className = 'msg ' + (isOwn ? 'msg--own' : 'msg--other');

        const meta = document.createElement('div');
        meta.className   = 'msg__meta';
        meta.textContent = (isOwn ? 'You' : msg.sender) + '  ' + formatTime(msg.timestamp);

        const text = document.createElement('div');
        text.textContent = msg.content;

        bubble.appendChild(meta);
        bubble.appendChild(text);
        messageArea.appendChild(bubble);
    } else {
        const sys = document.createElement('div');
        sys.className    = 'msg msg--system';
        const action     = msg.type === 'JOIN' ? 'joined' : 'left';
        sys.textContent  = msg.sender + ' ' + action + ' the chat';
        messageArea.appendChild(sys);
    }

    messageArea.scrollTop = messageArea.scrollHeight;
}

/* ── Load chat history via REST before the WebSocket connects ────────────── */
fetch('/api/messages')
    .then(res => res.json())
    .then(messages => messages.forEach(appendMessage))
    .catch(err => console.error('Failed to load chat history:', err));

/* ── Connect via SockJS + STOMP ──────────────────────────────────────────── */
function connect() {
    const socket = new SockJS('/chat-ws');
    stompClient  = Stomp.over(socket);
    stompClient.debug = null;

    stompClient.connect({}, function onConnect() {
        stompClient.subscribe('/topic/public', function onMessage(frame) {
            appendMessage(JSON.parse(frame.body));
        });

        stompClient.send('/app/chat.addUser', {}, JSON.stringify({
            sender: window.CURRENT_USER,
            type:   'JOIN'
        }));

        sendBtn.disabled = false;
    });
}

/* ── Send a chat message ─────────────────────────────────────────────────── */
function sendMessage() {
    const text = messageInput.value.trim();
    if (!text || !stompClient || !stompClient.connected) return;

    stompClient.send('/app/chat.sendMessage', {}, JSON.stringify({
        sender:  window.CURRENT_USER,
        content: text,
        type:    'CHAT'
    }));

    messageInput.value = '';
}

/* ── Event listeners ─────────────────────────────────────────────────────── */
document.getElementById('logoutBtn').addEventListener('click', function () {
    if (stompClient && stompClient.connected) {
        stompClient.send('/app/chat.addUser', {}, JSON.stringify({
            sender: window.CURRENT_USER,
            type:   'LEAVE'
        }));
        // Give the STOMP frame ~150 ms to flush before tearing down the socket.
        setTimeout(function () {
            stompClient.disconnect(function () {
                document.getElementById('logoutForm').submit();
            });
        }, 150);
    } else {
        document.getElementById('logoutForm').submit();
    }
});

sendBtn.addEventListener('click', sendMessage);

messageInput.addEventListener('keydown', function (e) {
    if (e.key === 'Enter' && !e.shiftKey) {
        e.preventDefault();
        sendMessage();
    }
});

/* ── Best-effort LEAVE on tab/window close ───────────────────────────────── */
window.onbeforeunload = function () {
    if (stompClient && stompClient.connected) {
        stompClient.send('/app/chat.addUser', {}, JSON.stringify({
            sender: window.CURRENT_USER,
            type:   'LEAVE'
        }));
        stompClient.disconnect();
    }
};

/* ── Kick off the connection ─────────────────────────────────────────────── */
connect();
