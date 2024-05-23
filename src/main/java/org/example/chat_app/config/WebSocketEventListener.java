package org.example.chat_app.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.chat_app.chat.ChatMessage;
import org.example.chat_app.chat.MessageType;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@Slf4j // Tạo logger tự động
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messageTemplate; // SimpMessageSendingOperations để gửi các thông điệp (messages) đến các điểm đích (destinations) được chỉ định

    @EventListener // Đánh dấu method này là 1 listener, sẽ được gọi khi một sự kiện cụ thể xảy ra.
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) { // Trong trường hợp này, sự kiện là SessionDisconnectEvent, được kích hoạt khi một phiên WebSocket ngắt kết nối.
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage()); // StompHeaderAccessor là một lớp trợ giúp để làm việc với các header của thông điệp STOMP.
        String username = (String) headerAccessor.getSessionAttributes().get("username"); // lấy username
        if (username != null) {
            log.info("User disconnected: {}", username);
            var chatMessage = ChatMessage.builder()
                    .type(MessageType.LEAVE)
                    .sender(username)
                    .build();
            messageTemplate.convertAndSend("/topic/public", chatMessage); // Convert đối tượng java chatMesage thành một thông điệp (message) và gửi đến đích /topic/public
        }
    }
}
