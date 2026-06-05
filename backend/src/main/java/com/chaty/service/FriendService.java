package com.chaty.service;

import com.chaty.model.Friend;
import com.chaty.repository.FriendRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FriendService {

    @Autowired
    private FriendRepository friendRepository;

    public Friend sendRequest(String senderId, String receiverId) {
        if (friendRepository.existsBySenderIdAndReceiverId(senderId, receiverId) ||
            friendRepository.existsBySenderIdAndReceiverId(receiverId, senderId)) {
            throw new RuntimeException("Friend request already exists or you are already friends.");
        }

        Friend friend = new Friend(senderId, receiverId, "pending");
        return friendRepository.save(friend);
    }

    public Friend acceptRequest(String requestId) {
        Optional<Friend> friendOpt = friendRepository.findById(requestId);
        if (friendOpt.isPresent()) {
            Friend friend = friendOpt.get();
            friend.setStatus("accepted");
            return friendRepository.save(friend);
        } else {
            throw new RuntimeException("Friend request not found.");
        }
    }

    public List<Friend> getFriendsList(String userId) {
        return friendRepository.findBySenderIdOrReceiverId(userId, userId).stream()
                .filter(f -> "accepted".equals(f.getStatus()))
                .toList();
    }
    
    public List<Friend> getPendingRequests(String userId) {
        return friendRepository.findByReceiverIdAndStatus(userId, "pending");
    }
}
