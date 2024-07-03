package com.matosic.Facebook.controller;

import com.matosic.Facebook.dto.ChangePasswordRequest;
import com.matosic.Facebook.dto.LoginRequest;
import com.matosic.Facebook.dto.PostDto;
import com.matosic.Facebook.dto.RegistrationRequest;
import com.matosic.Facebook.model.Post;
import com.matosic.Facebook.model.User;
import com.matosic.Facebook.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/users")
@Slf4j
@CrossOrigin("http://localhost:3000/")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody RegistrationRequest registrationRequest) {
        log.info("Registering user with email: {}", registrationRequest.getEmail());
        User user = userService.registerUser(registrationRequest);
        log.info("User registered successfully: {}", user.getUsername());
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<User> loginUser(@RequestBody LoginRequest credentials) {
        log.info("Attempting to log in user: {}", credentials.getUsername());
        User user = userService.loginUser(credentials.getUsername(), credentials.getPassword());
        if (user == null) {
            log.warn("Login failed for user: {}", credentials.getUsername());
            return ResponseEntity.status(401).build();
        }
        log.info("User logged in successfully: {}", credentials.getUsername());
        return ResponseEntity.ok(user);
    }
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        boolean success = userService.changePassword(
                changePasswordRequest.getUsername(),
                changePasswordRequest.getOldPassword(),
                changePasswordRequest.getNewPassword());

        if (success) {
            return ResponseEntity.ok("Password changed successfully.");
        } else {
            return ResponseEntity.badRequest().body("Failed to change password. Please check your credentials.");
        }
    }
    
    @GetMapping("/get-friends/{userId}")
    public List<User> getUserFriends(@PathVariable Long userId) {

        return userService.getFriends(userId);
    }
    
    @GetMapping("/get-all")
    public List<User> getAll() {

        return userService.findAll();
    }
    
    @GetMapping("/{userId}/friends-and-groups-posts")
    public List<PostDto> getFriendsAndGroupsPosts(@PathVariable Long userId) {
        List<Post> posts = userService.getFriendsAndGroupsPosts(userId);
        return posts.stream().map(PostDto::new).collect(Collectors.toList());
    }
}
