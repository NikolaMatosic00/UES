package com.matosic.Facebook.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.matosic.Facebook.model.FriendRequest;
import com.matosic.Facebook.service.FriendRequestService;

@RestController
@RequestMapping("/api/friend-requests")
@CrossOrigin("http://localhost:3000/")
public class FriendRequestController {

	@Autowired
	private FriendRequestService friendRequestService;

	@PostMapping("/send")
	public ResponseEntity<FriendRequest> sendFriendRequest(@RequestParam Long fromUserId, @RequestParam Long toUserId) {
		FriendRequest friendRequest = friendRequestService.sendFriendRequest(fromUserId, toUserId);
		return ResponseEntity.ok(friendRequest);
	}

	@PostMapping("/accept/{friendRequestId}")
	public ResponseEntity<String> acceptFriendRequest(@PathVariable Long friendRequestId) {
		boolean accepted = friendRequestService.acceptFriendRequest(friendRequestId);
		if (accepted) {
			return ResponseEntity.ok("Friend request accepted successfully.");
		} else {
			return ResponseEntity.badRequest().body("Friend request could not be accepted.");
		}
	}

	@GetMapping("/sent")
	public ResponseEntity<List<FriendRequest>> getFriendRequestsSentByUser(@RequestParam Long userId) {
		List<FriendRequest> friendRequests = friendRequestService.findFriendRequestsSentByUser(userId);
		return ResponseEntity.ok(friendRequests);
	}
}
