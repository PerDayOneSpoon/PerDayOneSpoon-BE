package com.sparta.perdayonespoon.repository;

import com.sparta.perdayonespoon.domain.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long>, FriendRepositoryCustom {
    Optional<Friend> findByFollowerIdAndFollowingId(String user, String checkUser);

    List<Friend> findAllByFollowingId(String socialId);

    List<Friend> findAllByFollowerId(String socialId);
}
