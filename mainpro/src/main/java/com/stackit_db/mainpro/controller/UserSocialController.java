package com.stackit_db.mainpro.controller;

import com.stackit_db.mainpro.dto.UserProfileDTO;
import com.stackit_db.mainpro.dto.UserUpdateRequestDTO;
import com.stackit_db.mainpro.entity.User;
import com.stackit_db.mainpro.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserSocialController {

    private final com.stackit_db.mainpro.repository.UserRepository userRepository;
    private final FollowService followService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getProfile(@PathVariable Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found"));
        return ResponseEntity.ok(toProfile(user));
    }

    @PostMapping("/update")
    @Transactional
    public ResponseEntity<?> updateProfile(@RequestBody UserUpdateRequestDTO req) {
        if (req == null || req.getUserId() == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "userId is required"));
        }

        User user = userRepository.findById(req.getUserId()).orElse(null);
        if (user == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found"));

        if (StringUtils.hasText(req.getDisplayName())) user.setDisplayName(req.getDisplayName().trim());
        if (req.getAvatarUrl() != null) user.setAvatarUrl(req.getAvatarUrl().trim());

        userRepository.save(user);
        return ResponseEntity.ok(toProfile(user));
    }

    @GetMapping("/{id}/followers")
    public ResponseEntity<?> getFollowers(@PathVariable Long id) {
        if (!userRepository.existsById(id)) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found"));
        return ResponseEntity.ok(followService.getFollowerUsers(id));
    }

    private UserProfileDTO toProfile(User u) {
        String displayName = (u.getDisplayName() == null || u.getDisplayName().isBlank()) ? u.getUsername() : u.getDisplayName();
        return new UserProfileDTO(
                u.getId(),
                u.getUsername(),
                displayName,
                u.getAvatarUrl(),
                u.getBio(),
                u.getFollowersCount(),
                u.getFollowingCount()
        );
    }
}
