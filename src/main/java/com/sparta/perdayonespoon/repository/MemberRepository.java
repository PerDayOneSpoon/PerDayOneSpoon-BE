package com.sparta.perdayonespoon.repository;

import com.sparta.perdayonespoon.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findBySocialId(String id);
//    Optional<Member> findByUserId(String username);
//    socialEmail

//    Optional<Member> findByEmail(String email);
}
