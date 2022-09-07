package com.sparta.perdayonespoon.testrunner;

import com.sparta.perdayonespoon.domain.Authority;
import com.sparta.perdayonespoon.domain.Member;
import com.sparta.perdayonespoon.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class TestDataRunner implements ApplicationRunner {

    MemberRepository memberRepository;

    PasswordEncoder passwordEncoder;

    @Autowired
    public TestDataRunner(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
// 테스트 User 생성
        List<Member> memberList = new ArrayList<>();
        for(int i=0; i<15; i++) {
            memberList.add(Member.builder()
                    .socialId(UUID.randomUUID().toString())
                    .authority(Authority.ROLE_USER)
                    .nickname("바아지")
                    .socialCode(UUID.randomUUID().toString().substring(0,5))
                    .email("dfsdlkj@naver.com")
                    .password("sdfsdfw")
                    .build());
        }
        memberRepository.saveAll(memberList);
    }
}