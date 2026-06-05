package com.chaty.controller;

import com.chaty.dto.MessageDto;
import com.chaty.model.Conversation;
import com.chaty.model.Message;
import com.chaty.repository.ConversationRepository;
import com.chaty.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private MessageService messageService;

    @Autowired
    private ConversationRepository conversationRepository;

    @MessageMapping("/send")
    public void sendMessage(@Payload MessageDto messageDto) {
        try {
            // Process, translate, and save the message
            Message savedMessage = messageService.processAndSaveMessage(messageDto);

            Conversation conversation = conversationRepository.findById(messageDto.getConversationId())
                    .orElseThrow(() -> new RuntimeException("Conversation not found"));

            // Find the receiver
            String receiverId = conversation.getMembers().stream()
                    .filter(id -> !id.equals(messageDto.getSenderId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Receiver not found"));

            // Send to the specific user's queue
            // Clients should subscribe to: /topic/messages/{userId}
            messagingTemplate.convertAndSend(
                    "/topic/messages/" + receiverId, 
                    savedMessage
            );
            
            // Also send back to sender so they get their own message on their UI
            messagingTemplate.convertAndSend(
                    "/topic/messages/" + messageDto.getSenderId(), 
                    savedMessage
            );

        } catch (Exception e) {
            // Log any critical errors during WebSocket processing
            System.err.println("WebSocket Error: " + e.getMessage());
        }
    }
}
