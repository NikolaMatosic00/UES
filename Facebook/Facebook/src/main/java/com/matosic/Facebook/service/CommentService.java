package com.matosic.Facebook.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.matosic.Facebook.model.Comment;
import com.matosic.Facebook.model.Post;
import com.matosic.Facebook.model.User;
import com.matosic.Facebook.repository.elasticsearch.ElasticPostRepository;
import com.matosic.Facebook.repository.jpa.CommentRepository;
import com.matosic.Facebook.repository.jpa.PostRepository;
import com.matosic.Facebook.repository.jpa.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    public Comment addComment(Long postId, Long userId, String text) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

        Comment comment = new Comment();
        comment.setText(text);
        comment.setTimestamp(LocalDate.now());
        comment.setDeleted(false);
        comment.setUser(user);
        comment.setPost(post);
        
        log.info("Comment added to post ID: {}, by user ID: {}", postId, userId);

        return commentRepository.save(comment);
    }

    public Comment replyToComment(Long parentCommentId, Long userId, String text) {
        Comment parentComment = commentRepository.findById(parentCommentId).orElseThrow(() -> new IllegalArgumentException("Invalid comment ID"));
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

        Comment reply = new Comment();
        reply.setText(text);
        reply.setTimestamp(LocalDate.now());
        reply.setDeleted(false);
        reply.setUser(user);
        reply.setPost(parentComment.getPost());
        reply.setParentComment(parentComment);

        log.info("Reply added to comment ID: {}, by user ID: {}", parentCommentId, userId);

        return commentRepository.save(reply);
    }
    
    public boolean updateComment(Long commentId, Long userId, String newText) {
        // Fetch the comment and user
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        if (optionalComment.isPresent()) {
            Comment comment = optionalComment.get();
            if (comment.getUser().getId().equals(userId)) {
                comment.setText(newText);
                commentRepository.save(comment);
                log.info("Comment with ID {} updated by user ID {}", commentId, userId);
                return true;
            } else {
                log.warn("User with ID {} is not authorized to update comment with ID {}", userId, commentId);
                return false;
            }
        } else {
            log.warn("Comment with ID {} not found", commentId);
            return false;
        }
    }
}
