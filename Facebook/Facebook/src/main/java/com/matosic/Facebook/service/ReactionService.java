package com.matosic.Facebook.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.matosic.Facebook.model.Comment;
import com.matosic.Facebook.model.Post;
import com.matosic.Facebook.model.Reaction;
import com.matosic.Facebook.model.ReactionType;
import com.matosic.Facebook.model.User;
import com.matosic.Facebook.repository.elasticsearch.ElasticPostRepository;
import com.matosic.Facebook.repository.jpa.CommentRepository;
import com.matosic.Facebook.repository.jpa.PostRepository;
import com.matosic.Facebook.repository.jpa.ReactionRepository;
import com.matosic.Facebook.repository.jpa.UserRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReactionService {

    @Autowired
    private ReactionRepository reactionRepository;
    
    @Autowired
    private PostRepository postRepository;
    
    
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Reaction addReaction(Long postId, Long userId, ReactionType reactionType) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

        Reaction reaction = new Reaction();
        reaction.setType(reactionType);
        reaction.setTimestamp(LocalDateTime.now());
        reaction.setPost(post);
        reaction.setUser(user);

        // Save the reaction
        reaction = reactionRepository.save(reaction);

        // Add the reaction to the post's set of reactions
        post.getReactions().add(reaction);
        postRepository.save(post);

        return reaction;
    }
    
    public Reaction addReactionToComment(Long commentId, Long userId, ReactionType type) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("Invalid comment ID"));
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

        Reaction reaction = new Reaction();
        reaction.setType(type);
        reaction.setTimestamp(LocalDateTime.now());
        reaction.setUser(user);
        reaction.setComment(comment);
        
        log.info("User '{}' reacted with '{}' to comment '{}'", user.getUsername(), type, comment.getId());

        return reactionRepository.save(reaction);
    }
}
