package org.example.chat_app.chat;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    @MessageMapping("/chat.sendMessage") // Đã đăng ký ApplicationDestinationPrefixes là /app nên khi một client gửi một thông điệp STOMP đến điểm đích /app/chat.sendMessage, method này sẽ xử lý
    @SendTo("/topic/public") // kết quả trả về của phương thức này sẽ được gửi đến tất cả các client đã đăng ký (subscribed) vào điểm đích /topic/public.
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor) { // SimpMessageHeaderAccessor giúp xử lý các chi tiết cụ thể của tiêu đề thông điệp, chẳng hạn như session ID, user ID,...
        // Thêm username vào WebSocket session
        headerAccessor.getSessionAttributes() // trả về một Map<String, Object> chứa các thuộc tính phiên (session attributes) của phiên WebSocket hiện tại.
                .put("username", chatMessage.getSender()); // put thêm một cặp khóa-giá trị (key-value) vào Map. Ở đây, key là "username", value là chatMessage.getSender()
        return chatMessage;
    }
}
