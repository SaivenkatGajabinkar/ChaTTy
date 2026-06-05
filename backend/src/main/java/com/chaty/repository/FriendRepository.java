package com.chaty.repository;

import com.chaty.model.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendRepository extends JpaRepository<Friend, String> {
    List<Friend> findBySenderIdOrReceiverId(String senderId, String receiverId);
    List<Friend> findByReceiverIdAndStatus(String receiverId, String status);
    boolean existsBySenderIdAndReceiverId(String senderId, String receiverId);
}
