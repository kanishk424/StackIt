package com.stackit_db.mainpro.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserProfileDTO {
    private Long id;
    private String username;
    private String displayName;
    private String avatarUrl;
    private String bio;
    private Integer followersCount;
    private Integer followingCount;
}
