package com.stackit_db.mainpro.repository;

import com.stackit_db.mainpro.entity.User;
import com.stackit_db.mainpro.entity.UserFollow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserFollowRepository extends JpaRepository<UserFollow, Long> {
    List<UserFollow> findByFollowing(User following);
    Optional<UserFollow> findByFollowerAndFollowing(User follower, User following);
    boolean existsByFollowerAndFollowing(User follower, User following);
}
