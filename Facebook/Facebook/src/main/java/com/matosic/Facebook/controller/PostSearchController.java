package com.matosic.Facebook.controller;

import com.matosic.Facebook.model.Post;
import com.matosic.Facebook.service.PostSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/posts/elastic")
@CrossOrigin("http://localhost:3000/")
public class PostSearchController {

    @Autowired
    private PostSearchService postSearchService;

    @GetMapping("/search")
    public List<Post> searchPosts(
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String pdfContent,
            @RequestParam(required = false) Integer minLikes,
            @RequestParam(required = false) Integer maxLikes,
            @RequestParam(required = false) String queryType) {

        return postSearchService.searchPosts(content, pdfContent, minLikes, maxLikes, queryType);
    }
}
