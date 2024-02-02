package com.example.websocketdemo.controller;

import com.example.websocketdemo.model.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by rajeevkumarsingh on 25/07/17.
 */
@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

//    @EventListener
//    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
//        logger.info("Received a new web socket connection");
//    }
//
//    @EventListener
//    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
//        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
//
//        String username = (String) headerAccessor.getSessionAttributes().get("username");
//        if(username != null) {
//            logger.info("User Disconnected : " + username);
//
//            ChatMessage chatMessage = new ChatMessage();
//            chatMessage.setType(ChatMessage.MessageType.LEAVE);
//            chatMessage.setSender(username);
//
//            messagingTemplate.convertAndSend("/topic/public", chatMessage);
//        }
//    }

    // Lưu trữ lịch sử tin nhắn
    private List<ChatMessage> chatHistory = Collections.synchronizedList(new ArrayList<>());

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.info("Received a new web socket connection");

        // Gửi lịch sử tin nhắn cho người dùng mới kết nối
        List<ChatMessage> chatMessages = getChatHistory();
        messagingTemplate.convertAndSendToUser("admin", "/topic/history", chatMessages);
//        messagingTemplate.convertAndSend("/topic/public", chatMessages);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String username = getUsernameFromEvent(event);
        if (username != null) {
            logger.info("User Disconnected: " + username);

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(ChatMessage.MessageType.LEAVE);
            chatMessage.setSender(username);

            messagingTemplate.convertAndSend("/topic/public", chatMessage);

            // Lưu tin nhắn rời đi vào lịch sử
            chatHistory.add(chatMessage);
        }
    }

    private String getUsernameFromEvent(AbstractSubProtocolEvent event) {
        // Trích xuất tên người dùng từ sự kiện, cần điều chỉnh tùy thuộc vào cách bạn lưu trữ thông tin người dùng
        // Đây là một ví dụ đơn giản, bạn cần thay thế bằng cách phù hợp cho ứng dụng của bạn
        return (String) event.getMessage().getHeaders().get("simpUser");
    }

    private List<ChatMessage> getChatHistory() {
        // Trả về lịch sử chat (có thể lấy từ cơ sở dữ liệu hoặc service của bạn)
        return new ArrayList<>(chatHistory);
    }
}
