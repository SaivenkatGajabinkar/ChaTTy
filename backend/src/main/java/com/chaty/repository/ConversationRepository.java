package com.chaty.repository;

import com.chaty.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, String> {
    
    List<Conversation> findByMembersContaining(String userId);
}
