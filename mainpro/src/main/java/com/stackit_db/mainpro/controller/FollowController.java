package com.stackit_db.mainpro.controller;

import com.stackit_db.mainpro.dto.CircleUserDTO;
import com.stackit_db.mainpro.dto.SocialFollowRequestDTO;
import com.stackit_db.mainpro.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/social")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @GetMapping("/followers/{userId}")
    public ResponseEntity<List<CircleUserDTO>> getFollowers(@PathVariable Long userId) {
        return ResponseEntity.ok(followService.getFollowerUsers(userId));
    }


    @PostMapping("/follow")
    @Transactional
    public ResponseEntity<?> follow(@RequestBody SocialFollowRequestDTO req) {
        if (req == null || req.getFollowerId() == null || req.getFollowingId() == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "followerId and followingId are required"));
        }
        try {
            followService.follow(req.getFollowerId(), req.getFollowingId());
            return ResponseEntity.ok(Map.of("message", "Followed successfully"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", ex.getMessage()));
        }
    }

    @DeleteMapping("/follow")
    @Transactional
    public ResponseEntity<?> unfollow(@RequestBody SocialFollowRequestDTO req) {
        if (req == null || req.getFollowerId() == null || req.getFollowingId() == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "followerId and followingId are required"));
        }
        try {
            followService.unfollow(req.getFollowerId(), req.getFollowingId());
            return ResponseEntity.ok(Map.of("message", "Unfollowed successfully"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", ex.getMessage()));
        }
    }
}
