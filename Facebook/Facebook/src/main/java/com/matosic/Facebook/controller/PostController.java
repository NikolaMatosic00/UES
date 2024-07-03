package com.matosic.Facebook.controller;

import com.matosic.Facebook.dto.CreatePostRequest;
import com.matosic.Facebook.model.Post;
import com.matosic.Facebook.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin("http://localhost:3000/")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping("/create")
    public ResponseEntity<Post> createPost(@ModelAttribute CreatePostRequest createPostRequest) {
        Post post = postService.createPost(createPostRequest);
        return ResponseEntity.ok(post);
    }
}
