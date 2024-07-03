package com.matosic.Facebook.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.matosic.Facebook.dto.UserGroupDTO;
import com.matosic.Facebook.model.User;
import com.matosic.Facebook.model.UserGroup;
import com.matosic.Facebook.service.UserGroupService;
import com.matosic.Facebook.service.UserService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/groups")
@Slf4j
@CrossOrigin("http://localhost:3000/")
public class UserGroupController {

    @Autowired
    private UserGroupService userGroupService;

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<String> createGroup(@ModelAttribute UserGroupDTO userGroupDTO) {
        try {
            User admin = userService.findById(userGroupDTO.getAdminId());
            if (admin == null) {
                return ResponseEntity.badRequest().body(null);
            }
            userGroupService.createUserGroup(userGroupDTO, admin);
            return ResponseEntity.ok("created");
        } catch (IOException e) {
            log.error("Failed to create group", e);
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/suspend/{groupId}")
    public ResponseEntity<String> suspendGroup(@PathVariable Long groupId, @RequestParam String reason) {
        userGroupService.suspendGroup(groupId, reason);
        return ResponseEntity.ok("Group suspended successfully.");
    }
    
    @PostMapping("/send-join-request")
    public ResponseEntity<String> sendGroupJoinRequest(@RequestParam Long groupId, @RequestParam Long userId) {
        boolean requestSent = userGroupService.sendGroupJoinRequest(groupId, userId);
        
        if (requestSent) {
            return ResponseEntity.ok("Join request sent successfully.");
        } else {
            return ResponseEntity.badRequest().body("Failed to send join request.");
        }
    }

    
    @PostMapping("/requests/approve/{requestId}")
    public ResponseEntity<String> addUserToGroup(@PathVariable Long requestId) {
    	userGroupService.approveJoinRequestAndAddUserToGroup(requestId);
    	return ResponseEntity.ok("User added to group successfully.");
    }


    @PostMapping("/requests/reject/{requestId}")
    public ResponseEntity<String> rejectJoinRequest(@PathVariable Long requestId) {
        boolean rejected = userGroupService.rejectJoinRequest(requestId);
        if (rejected) {
            return ResponseEntity.ok("Join request rejected.");
        } else {
            return ResponseEntity.badRequest().body("Invalid join request ID.");
        }
    }
}
