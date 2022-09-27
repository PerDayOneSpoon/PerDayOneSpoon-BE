package com.sparta.perdayonespoon.service;

import com.sparta.perdayonespoon.domain.Badge;
import com.sparta.perdayonespoon.jwt.Principaldetail;
import com.sparta.perdayonespoon.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BadgeService {

    private final MemberRepository memberRepository;
    public ResponseEntity getMyBadge(Principaldetail principaldetail) {
        List<Badge> badgeList = memberRepository.findByMemberId(principaldetail.getMember().getId())
                .orElseThrow(() -> new IllegalArgumentException("해당 뱃지들이 없습니다."))
                .getBadgeList();
        return ResponseEntity.ok().body(badgeList);
    }

}
