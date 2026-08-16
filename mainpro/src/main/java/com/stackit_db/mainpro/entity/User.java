package com.stackit_db.mainpro.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "display_name", length = 80)
    private String displayName;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false)
    private Integer reputation = 0;

    @Column(nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "followers_count", nullable = false)
    private Integer followersCount = 0;

    @Column(name = "following_count", nullable = false)
    private Integer followingCount = 0;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = new Timestamp(System.currentTimeMillis());
        if (reputation == null) reputation = 0;
        if (followersCount == null) followersCount = 0;
        if (followingCount == null) followingCount = 0;
        if (bio == null) bio = "";
        if (displayName == null || displayName.isBlank()) displayName = username;
    }
}
