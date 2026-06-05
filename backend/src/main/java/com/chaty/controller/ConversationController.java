package com.chaty.controller;

import com.chaty.model.Conversation;
import com.chaty.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/conversation")
@CrossOrigin(origins = "*")
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    @PostMapping("/create")
    public ResponseEntity<Conversation> createConversation(@RequestParam String user1Id, @RequestParam String user2Id) {
        Conversation conversation = conversationService.createOrGetConversation(user1Id, user2Id);
        return ResponseEntity.ok(conversation);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Conversation>> getUserConversations(@PathVariable String userId) {
        return ResponseEntity.ok(conversationService.getUserConversations(userId));
    }
}
