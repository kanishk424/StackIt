package com.stackit_db.mainpro.service;

import com.stackit_db.mainpro.dto.CircleUserDTO;
import com.stackit_db.mainpro.entity.Follow;
import com.stackit_db.mainpro.entity.FollowId;
import com.stackit_db.mainpro.entity.User;
import com.stackit_db.mainpro.repository.FollowRepository;
import com.stackit_db.mainpro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<CircleUserDTO> getFollowingUsers(Long userId) {
        if (userId == null) throw new IllegalArgumentException("userId is required");
        return followRepository.findFollowingByFollowerId(userId).stream().map(this::toCircleDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<CircleUserDTO> getFollowerUsers(Long userId) {
        if (userId == null) throw new IllegalArgumentException("userId is required");
        return followRepository.findFollowersByFollowingId(userId).stream().map(this::toCircleDTO).toList();
    }

    @Transactional
    public void follow(Long followerId, Long followingId) {
        if (followerId == null || followingId == null) throw new IllegalArgumentException("Follower and following IDs are required");
        if (followerId.equals(followingId)) throw new IllegalArgumentException("Cannot follow yourself");
        if (followRepository.existsByFollower_IdAndFollowing_Id(followerId, followingId)) return;

        User follower = userRepository.findById(followerId).orElseThrow(() -> new IllegalArgumentException("Follower not found"));
        User following = userRepository.findById(followingId).orElseThrow(() -> new IllegalArgumentException("Following user not found"));

        Follow follow = new Follow();
        follow.setId(new FollowId(followerId, followingId));
        follow.setFollower(follower);
        follow.setFollowing(following);
        followRepository.save(follow);

        follower.setFollowingCount((follower.getFollowingCount() == null ? 0 : follower.getFollowingCount()) + 1);
        following.setFollowersCount((following.getFollowersCount() == null ? 0 : following.getFollowersCount()) + 1);
        userRepository.save(follower);
        userRepository.save(following);
    }

    @Transactional
    public void unfollow(Long followerId, Long followingId) {
        Follow follow = followRepository.findByIdFollowerIdAndIdFollowingId(followerId, followingId)
                .orElseThrow(() -> new IllegalArgumentException("Follow relation not found"));

        User follower = follow.getFollower();
        User following = follow.getFollowing();

        followRepository.delete(follow);

        follower.setFollowingCount(Math.max(0, (follower.getFollowingCount() == null ? 0 : follower.getFollowingCount()) - 1));
        following.setFollowersCount(Math.max(0, (following.getFollowersCount() == null ? 0 : following.getFollowersCount()) - 1));
        userRepository.save(follower);
        userRepository.save(following);
    }

    private CircleUserDTO toCircleDTO(User user) {
        String displayName = (user.getDisplayName() == null || user.getDisplayName().isBlank()) ? user.getUsername() : user.getDisplayName();
        return new CircleUserDTO(
                user.getId(),
                displayName,
                user.getAvatarUrl(),
                user.getBio(),
                user.getFollowersCount(),
                user.getFollowingCount()
        );
    }
}
