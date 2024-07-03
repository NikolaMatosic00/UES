package com.matosic.Facebook.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.matosic.Facebook.model.FriendRequest;
import com.matosic.Facebook.model.User;
import com.matosic.Facebook.repository.jpa.FriendRequestRepository;
import com.matosic.Facebook.repository.jpa.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FriendRequestService {

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public FriendRequest sendFriendRequest(Long fromUserId, Long toUserId) {
        User fromUser = userRepository.findById(fromUserId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid fromUser ID"));
        User toUser = userRepository.findById(toUserId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid toUser ID"));

     // Provera da li su već prijatelji
        if (fromUser.getFriends().contains(toUser)) {
            throw new IllegalArgumentException("Users are already friends");
        }
        
        FriendRequest friendRequest = new FriendRequest(false, LocalDateTime.now(), null, fromUser, toUser);
        log.info("Friend request sent from user {} to user {}", fromUserId, toUserId);
        return friendRequestRepository.save(friendRequest);
    }

    @Transactional
    public boolean acceptFriendRequest(Long friendRequestId) {
        FriendRequest friendRequest = friendRequestRepository.findById(friendRequestId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid friendRequest ID"));

        if (friendRequest.isApproved()) {
            log.warn("Friend request with ID {} is already approved", friendRequestId);
            return false;
        }

        friendRequest.setApproved(true);
        friendRequest.setAcceptedAt(LocalDateTime.now());
        User fromUser = friendRequest.getFromUser();
        User toUser = friendRequest.getToUser();

        fromUser.getFriends().add(toUser);
        toUser.getFriends().add(fromUser);
        log.info("Friend request with ID {} accepted", friendRequestId);

        // Perform additional logic for establishing friendship (not shown here)

        return true;
    }
    
    public List<FriendRequest> findFriendRequestsSentByUser(Long userId) {
        return friendRequestRepository.findByFromUserId(userId);
    }
}
