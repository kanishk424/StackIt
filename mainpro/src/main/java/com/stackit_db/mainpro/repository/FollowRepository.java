package com.stackit_db.mainpro.repository;

import com.stackit_db.mainpro.entity.Follow;
import com.stackit_db.mainpro.entity.FollowId;
import com.stackit_db.mainpro.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, FollowId> {

    @Query("select f.following from Follow f where f.follower.id = :followerId")
    List<User> findFollowingByFollowerId(@Param("followerId") Long followerId);

    @Query("select f.follower from Follow f where f.following.id = :followingId")
    List<User> findFollowersByFollowingId(@Param("followingId") Long followingId);

    boolean existsByFollower_IdAndFollowing_Id(Long followerId, Long followingId);

    Optional<Follow> findByIdFollowerIdAndIdFollowingId(Long followerId, Long followingId);
}
