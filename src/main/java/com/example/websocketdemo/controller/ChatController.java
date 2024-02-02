package com.example.websocketdemo.controller;

import com.example.websocketdemo.model.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajeevkumarsingh on 24/07/17.
 */
@Controller
public class ChatController {

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        //TODO save message to DB
        System.out.println("ChatController.sendMessage: " + chatMessage.getContent());
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
//        System.out.println("ChatController.addUser: " + chatMessage.getContent());
        return chatMessage;
    }

    @MessageMapping("/chat.requestHistory")
    @SendToUser("/topic/history")
    public List<ChatMessage> requestHistory() {
        //TODO get retrieve chat history from DB
        List<ChatMessage> chatHistory = new ArrayList<>();
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(ChatMessage.MessageType.HISTORY);
        chatMessage.setSender("history");
        chatMessage.setContent("Welcome to the chat!");
        chatHistory.add(chatMessage);

        ChatMessage chatMessage2 = new ChatMessage();
        chatMessage2.setType(ChatMessage.MessageType.HISTORY);
        chatMessage2.setSender("history");
        chatMessage2.setContent("Hello, how are you?");
        chatHistory.add(chatMessage2);
        return chatHistory;
    }

}
