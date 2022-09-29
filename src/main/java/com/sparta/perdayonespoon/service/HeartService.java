package com.sparta.perdayonespoon.service;

import com.sparta.perdayonespoon.domain.Badge;
import com.sparta.perdayonespoon.domain.Goal;
import com.sparta.perdayonespoon.domain.Heart;
import com.sparta.perdayonespoon.domain.Member;
import com.sparta.perdayonespoon.domain.dto.HeartResponseDto;
import com.sparta.perdayonespoon.jwt.Principaldetail;
import com.sparta.perdayonespoon.repository.BadgeRepository;
import com.sparta.perdayonespoon.repository.HeartRepository;
import com.sparta.perdayonespoon.repository.MemberRepository;
import com.sparta.perdayonespoon.util.GenerateMsg;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class HeartService {

    private final BadgeRepository badgeRepository;
    private final HeartRepository heartRepository;

    private final MemberRepository memberRepository;

    @Transactional
    public ResponseEntity<HeartResponseDto> addHearts(Principaldetail principaldetail, String goalFlag) {
        List<Goal> goalList= heartRepository.findGoalsHeart(goalFlag,principaldetail.getMember().getSocialId());
        if(!goalList.isEmpty()) {
            if (goalList.get(0).getSocialId().equals(principaldetail.getMember().getSocialId()))
                throw new IllegalArgumentException("자신의 습관에 좋아요를 할 수 없습니다");
        }
        else
            {throw new IllegalArgumentException("해당 습관이 존재하지 않습니다.");}
        Member member = memberRepository.findBySocialId(principaldetail.getMember().getSocialId()).orElseThrow(
                () -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));
        if(goalList.get(0).getHeartList().stream().noneMatch(g->g.getSocialId().equals(principaldetail.getMember().getSocialId()))){

            List<Heart> newHeartList = new ArrayList<>();
            goalList.forEach(goal-> createHeart(goal,newHeartList,principaldetail));
            heartRepository.saveAll(newHeartList);

            HeartResponseDto heartResponseDto = HeartResponseDto.builder()
                    .heartCnt(goalList.get(0).getHeartList().size())
                    .heartCheck(true)
                    .msgDto(GenerateMsg.getMsg(HttpServletResponse.SC_OK,"친구를 응원하셨습니다.!!"))
                    .build();

            List<Badge> badgeList = new ArrayList<>();
            member.addClickCnt();
            Member badgeOwner = goalList.get(0).getMember();
            if(member.getHeartClickCnt() >= 10){
                if(member.getBadgeList().stream().noneMatch(b->b.getBadgeName().equals("긍정 뱃지"))){
                    badgeList.add(Badge.realBadgeBuilder()
                            .badgeName("긍정 뱃지")
                            .badgeNumber(9)
                            .member(member)
                            .createdAt(LocalDate.now())
                            .build());
                }
            }
            if(goalList.get(0).getHeartList().size() >= 10){
                if(badgeOwner.getBadgeList().stream().noneMatch(b->b.getBadgeName().equals("매력 뱃지"))){
                    badgeList.add(Badge.realBadgeBuilder()
                            .badgeName("매력 뱃지")
                            .badgeNumber(10)
                            .member(badgeOwner)
                            .createdAt(LocalDate.now())
                            .build());
                }
            }
            if(member.getBadgeList().size()>4){
                if(member.getBadgeList().stream().noneMatch(b->b.getBadgeName().equals("뱃지 왕 뱃지"))){
                    badgeList.add(Badge.realBadgeBuilder()
                            .badgeName("뱃지 왕 뱃지")
                            .member(member)
                            .createdAt(LocalDate.now())
                            .badgeNumber(5)
                            .build());
                }
            }
            if(badgeOwner.getBadgeList().size()>4){
                if(badgeOwner.getBadgeList().stream().noneMatch(b->b.getBadgeName().equals("뱃지 왕 뱃지"))){
                    badgeList.add(Badge.realBadgeBuilder()
                            .badgeName("뱃지 왕 뱃지")
                            .member(badgeOwner)
                            .createdAt(LocalDate.now())
                            .badgeNumber(5)
                            .build());
                }
            }
            if(!badgeList.isEmpty()){
                badgeRepository.saveAll(badgeList);
            }
            return ResponseEntity.ok().body(heartResponseDto);
        }else{
            List<Heart> heartList = goalList.stream()
                    .map(Goal::getHeartList)
                    .flatMap(Set::stream)
                    .filter(h->h.getSocialId().equals(principaldetail.getMember().getSocialId()))
                    .collect(Collectors.toList());

            int goalSize = goalList.size();
            for(int i =0; i<goalSize; i++){
                goalList.get(i).getHeartList().remove(heartList.get(i));
            }
            heartRepository.deleteAll(heartList);
            HeartResponseDto heartResponseDto = HeartResponseDto.builder()
                    .heartCnt(goalList.get(0).getHeartList().size())
                    .heartCheck(false)
                    .msgDto(GenerateMsg.getMsg(HttpServletResponse.SC_OK, "친구응원을 취소하셨습니다."))
                    .build();
            member.minusClickCnt();
            return ResponseEntity.ok().body(heartResponseDto);
        }
    }

    private void createHeart(Goal goal, List<Heart> heartList, Principaldetail principaldetail) {
        heartList.add(Heart.builder()
                .goal(goal)
                .socialId(principaldetail.getMember().getSocialId())
                .build());
    }
}
