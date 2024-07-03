package com.matosic.Facebook.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
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
public class GroupRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean approved;
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private UserGroup group;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Konstruktor sa logovanjem
    public GroupRequest(boolean approved, LocalDateTime createdAt, UserGroup group, User user) {
        this.approved = approved;
        this.createdAt = createdAt;
        this.group = group;
        this.user = user;
        log.info("GroupRequest object created with approved: {}, createdAt: {}, group: {}, user: {}", approved, createdAt, group, user);
    }
}
