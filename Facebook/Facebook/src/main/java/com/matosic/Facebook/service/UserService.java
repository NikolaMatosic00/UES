package com.matosic.Facebook.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j; // Dodajemo import za log objekat

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.matosic.Facebook.dto.RegistrationRequest;
import com.matosic.Facebook.model.Post;
import com.matosic.Facebook.model.User;
import com.matosic.Facebook.repository.jpa.UserRepository;

@Service
@Slf4j // Anotacija za Lombok log objekat
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
    }
    
    public List<User> findAll() {
        return userRepository.findAll();
                
    }

    public User registerUser(RegistrationRequest registrationRequest) {
        User user = new User();
        user.setUsername(registrationRequest.getUsername());
        user.setPassword(registrationRequest.getPassword()); // U stvarnom scenariju, hashuje se lozinka pre čuvanja
        user.setEmail(registrationRequest.getEmail());
        user.setFirstName(registrationRequest.getFirstName());
        user.setLastName(registrationRequest.getLastName());
        userRepository.save(user);
        log.info("User '{}' registered successfully.", user.getUsername());
        return user;
    }

    public User loginUser(String username, String password) {
        User user = userRepository.findByUsernameAndPassword(username, password);
        if (user != null) {
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
            log.info("User '{}' logged in.", user.getUsername());
        }
        return user;
    }
    
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        User user = userRepository.findByUsername(username);

        if (user != null && user.getPassword().equals(oldPassword)) {
            user.setPassword(newPassword);
            userRepository.save(user);
            log.info("User '{}' changed their password.", username);
            return true;
        } else {
            log.warn("Failed attempt to change password for user '{}'", username);
            return false;
        }
    }
    
    public List<User> getFriends(Long userId){
    	User user = userRepository.findById(userId).orElse(null);
    	List<User> firends = (List<User>) user.getFriends();
    	 return firends;
    }
    
    public List<Post> getFriendsAndGroupsPosts(Long userId) {
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

        List<Post> posts = user.getFriends().stream()
                .flatMap(friend -> friend.getPosts().stream())
                .collect(Collectors.toList());

        user.getGroups().forEach(group -> posts.addAll(group.getPosts()));

        posts.sort((post1, post2) -> post2.getCreationDate().compareTo(post1.getCreationDate()));

        return posts;
    }
    
}
