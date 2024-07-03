package com.matosic.Facebook.model;

import java.time.LocalDate;

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
public class Banned {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate timestamp;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private UserGroup group;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Konstruktor sa logovanjem
    public Banned(LocalDate timestamp, UserGroup group, User user) {
        this.timestamp = timestamp;
        this.group = group;
        this.user = user;
        log.info("Banned object created with timestamp: {}, group: {}, user: {}", timestamp, group, user);
    }
}
