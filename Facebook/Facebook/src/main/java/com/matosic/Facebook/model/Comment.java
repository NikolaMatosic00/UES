package com.matosic.Facebook.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;
    private LocalDate timestamp;
    private boolean isDeleted;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;

    @OneToMany(mappedBy = "parentComment")
    private Set<Comment> replies;

    @OneToMany(mappedBy = "comment")
    private Set<Reaction> reactions;

    @OneToMany(mappedBy = "comment")
    private Set<Report> reports;

    // Konstruktor sa logovanjem
    public Comment(String text, LocalDate timestamp, boolean isDeleted, User user, Post post, Comment parentComment) {
        this.text = text;
        this.timestamp = timestamp;
        this.isDeleted = isDeleted;
        this.user = user;
        this.post = post;
        this.parentComment = parentComment;
        log.info("Comment object created with text: {}, timestamp: {}, isDeleted: {}, user: {}, post: {}, parentComment: {}", text, timestamp, isDeleted, user, post, parentComment);
    }
}
