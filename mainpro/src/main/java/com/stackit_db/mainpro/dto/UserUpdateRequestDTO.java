package com.stackit_db.mainpro.dto;

import lombok.Data;

@Data
public class UserUpdateRequestDTO {
    private Long userId;
    private String displayName;
    private String avatarUrl;
}
