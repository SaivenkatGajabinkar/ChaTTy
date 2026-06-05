package com.chaty.controller;

import com.chaty.model.Message;
import com.chaty.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/message")
@CrossOrigin(origins = "*")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/{conversationId}")
    public ResponseEntity<List<Message>> getConversationMessages(@PathVariable String conversationId) {
        return ResponseEntity.ok(messageService.getConversationMessages(conversationId));
    }
}
