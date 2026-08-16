package com.stackit_db.mainpro.dto;

import lombok.Data;

@Data
public class SocialFollowRequestDTO {
    private Long followerId;
    private Long followingId;
}
