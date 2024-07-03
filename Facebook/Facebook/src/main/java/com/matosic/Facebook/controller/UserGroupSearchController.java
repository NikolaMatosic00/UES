package com.matosic.Facebook.controller;

import com.matosic.Facebook.model.UserGroup;
import com.matosic.Facebook.service.UserGroupSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/groups/elastic")
@CrossOrigin("http://localhost:3000/")
public class UserGroupSearchController {

    @Autowired
    private UserGroupSearchService userGroupSearchService;

    @GetMapping("/search")
    public List<UserGroup> searchUserGroups(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String pdfContent,
            @RequestParam(required = false) Integer minPosts,
            @RequestParam(required = false) Integer maxPosts,
            @RequestParam(required = false) String queryType) {

        return userGroupSearchService.searchUserGroups(name, description, pdfContent, minPosts, maxPosts, queryType);
    }
}
