package org.example.chat_app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") // Đăng ký một endpoint WebSocket tại đường dẫn /ws. Khi truy cập đến /ws sẽ tạo kết nối WebSocket
                .withSockJS(); // nếu trình duyệt không hỗ trợ WebSocket, SockJS sẽ sử dụng các kỹ thuật khác như long polling hoặc streaming để thay thế
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app"); // tiền tố cho các destination mà các message-mapping sẽ được định tuyến đến các phương thức xử lý (handler methods) trong controller.
        registry.enableSimpleBroker("/topic"); // Các thông điệp gửi đến các điểm đích bắt đầu với /topic sẽ được xử lý bởi broker và phân phối đến các client đã đăng ký (subscribed) vào các điểm đích đó.
//        registry.setUserDestinationPrefix("/user");
    }
}
