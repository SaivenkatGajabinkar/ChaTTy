package com.chaty.service;

import com.chaty.model.Conversation;
import com.chaty.repository.ConversationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ConversationService {

    @Autowired
    private ConversationRepository conversationRepository;

    public Conversation createOrGetConversation(String user1Id, String user2Id) {
        List<Conversation> conversations = conversationRepository.findByMembersContaining(user1Id);
        
        Optional<Conversation> existingConversation = conversations.stream()
                .filter(c -> c.getMembers().contains(user2Id))
                .findFirst();

        if (existingConversation.isPresent()) {
            return existingConversation.get();
        }

        Conversation newConversation = new Conversation(Arrays.asList(user1Id, user2Id));
        return conversationRepository.save(newConversation);
    }

    public List<Conversation> getUserConversations(String userId) {
        return conversationRepository.findByMembersContaining(userId);
    }
}
