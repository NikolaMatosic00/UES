package com.matosic.Facebook.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.Set;

@Document(indexName = "usergroup")
@Entity
@Data
@NoArgsConstructor
@Slf4j
public class UserGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Field(type = FieldType.Text, analyzer = "serbian")
    private String name;

    @Field(type = FieldType.Text, analyzer = "serbian")
    private String description;

    @Column(nullable = false)
    private LocalDateTime creationDate;

    @Column(nullable = false)
    private boolean isSuspended;

    private String suspendedReason;

    @JsonIgnore
    @ManyToMany(mappedBy = "groups")
    private Set<User> users;

    @OneToMany(mappedBy = "group")
    private Set<GroupRequest> groupRequests;

    @OneToMany(mappedBy = "group")
    private Set<Banned> banneds;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private User admin;

    @OneToMany(mappedBy = "userGroup")
    private Set<Post> posts;

    @Field(type = FieldType.Integer)
    private Integer postCount;

    @Field(type = FieldType.Text)
    private String pdfUrl; // Dodato polje za putanju ka PDF fajlu

    // Metodi za testiranje logovanja
    public void logGroupCreation() {
        log.info("Group created: {}", this);
    }

    public void logGroupSuspension() {
        log.info("Group suspended: {} - Reason: {}", name, suspendedReason);
    }
}
