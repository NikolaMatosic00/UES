package com.matosic.Facebook.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ReportReason reason;
    private LocalDate timestamp;
    private boolean accepted;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    // Konstruktor sa logovanjem
    public Report(ReportReason reason, LocalDate timestamp, boolean accepted, User user, Post post, Comment comment) {
        this.reason = reason;
        this.timestamp = timestamp;
        this.accepted = accepted;
        this.user = user;
        this.post = post;
        this.comment = comment;
        log.info("Report object created with reason: {}, timestamp: {}, accepted: {}, user: {}, post: {}, comment: {}", reason, timestamp, accepted, user, post, comment);
    }
}