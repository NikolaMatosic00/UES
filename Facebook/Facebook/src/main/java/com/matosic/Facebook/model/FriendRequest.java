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
public class FriendRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean approved;
    private LocalDateTime createdAt;
    private LocalDateTime acceptedAt;

    @ManyToOne
    @JoinColumn(name = "from_user_id")
    private User fromUser;

    @ManyToOne
    @JoinColumn(name = "to_user_id")
    private User toUser;

    // Konstruktor sa logovanjem
    public FriendRequest(boolean approved, LocalDateTime createdAt, LocalDateTime acceptedAt, User fromUser, User toUser) {
        this.approved = approved;
        this.createdAt = createdAt;
        this.acceptedAt = acceptedAt;
        this.fromUser = fromUser;
        this.toUser = toUser;
        log.info("FriendRequest object created with approved: {}, createdAt: {}, acceptedAt: {}, fromUser: {}, toUser: {}", approved, createdAt, acceptedAt, fromUser, toUser);
    }
}
