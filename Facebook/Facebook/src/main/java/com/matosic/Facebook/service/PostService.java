package com.matosic.Facebook.service;

import com.matosic.Facebook.dto.CreatePostRequest;
import com.matosic.Facebook.model.Post;
import com.matosic.Facebook.model.User;
import com.matosic.Facebook.model.UserGroup;
import com.matosic.Facebook.repository.elasticsearch.ElasticPostRepository;
import com.matosic.Facebook.repository.jpa.PostRepository;
import com.matosic.Facebook.repository.jpa.UserGroupRepository;
import com.matosic.Facebook.repository.jpa.UserRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@Slf4j
public class PostService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ElasticPostRepository elasticPostRepository;

    @Autowired
    private MinioService minioService;

    @Transactional
    public Post createPost(CreatePostRequest createPostRequest) {
        User user = userRepository.findById(createPostRequest.getUserId())
                                   .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

        UserGroup userGroup = null;
        if (createPostRequest.getUserGroupId() != null) {
            userGroup = userGroupRepository.findById(createPostRequest.getUserGroupId())
                                           .orElse(null);
        }

        MultipartFile pdfFile = createPostRequest.getPdfFile();
        String fileName = "post-" + System.currentTimeMillis() + ".pdf";
        String pdfUrl;
        try {
            pdfUrl = minioService.uploadFile(pdfFile, fileName);
        } catch (IOException e) {
            log.error("Failed to upload PDF file for post", e);
            throw new RuntimeException("Failed to upload PDF file", e);
        }

        Post post = new Post();
        post.setPdfUrl(pdfUrl);
        post.setCreationDate(LocalDateTime.now());
        post.setContent(createPostRequest.getContent());
        elasticPostRepository.save(post);
        post.setUser(user);
        post.setUserGroup(userGroup);

        // Save to MySQL
        postRepository.save(post);
        log.info("New post created in MySQL with PDF content URL: {}, user: {}", pdfUrl, user.getUsername());

        // Save to Elasticsearch
        log.info("New post indexed in Elasticsearch with ID: {}", post.getId());

        return post;
    }
}
