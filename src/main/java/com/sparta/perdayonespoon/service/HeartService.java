package com.sparta.perdayonespoon.service;

import com.sparta.perdayonespoon.domain.Badge;
import com.sparta.perdayonespoon.domain.Goal;
import com.sparta.perdayonespoon.domain.Heart;
import com.sparta.perdayonespoon.domain.Member;
import com.sparta.perdayonespoon.domain.dto.HeartResponseDto;
import com.sparta.perdayonespoon.jwt.Principaldetail;
import com.sparta.perdayonespoon.repository.BadgeRepository;
import com.sparta.perdayonespoon.repository.HeartRepository;
import com.sparta.perdayonespoon.util.GenerateMsg;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class HeartService {

    private final BadgeRepository badgeRepository;
    private final HeartRepository heartRepository;

    @Transactional
    public ResponseEntity<HeartResponseDto> addHearts(Principaldetail principaldetail, String goalFlag) {
        List<Goal> goalList= heartRepository.findGoalsHeart(goalFlag,principaldetail.getMember().getSocialId());
        if(!goalList.isEmpty()) {
            if (goalList.get(0).getSocialId().equals(principaldetail.getMember().getSocialId()))
                throw new IllegalArgumentException("자신의 습관에 좋아요를 할 수 없습니다");
        }
        else
            {throw new IllegalArgumentException("해당 습관이 존재하지 않습니다.");}
        if(goalList.get(0).getHeartList().isEmpty()){
            List<Heart> newHeartList = new ArrayList<>();
            goalList.forEach(goal-> createHeart(goal,newHeartList,principaldetail));
            heartRepository.saveAll(newHeartList);
            HeartResponseDto heartResponseDto = HeartResponseDto.builder()
                    .heartCnt(goalList.get(0).getHeartList().size())
                    .heartCheck(true)
                    .msgDto(GenerateMsg.getMsg(HttpServletResponse.SC_OK,"친구를 응원하셨습니다.!!"))
                    .build();
            List<Badge> badgeList = new ArrayList<>();
            goalList.get(0).getMember().addClickCnt();
            Member BadgeOwner = goalList.get(0).getMember();
            if(BadgeOwner.getHeartClickCnt() >= 10){
                if(BadgeOwner.getBadgeList().stream().noneMatch(b->b.getBadgeName().equals("긍정 뱃지"))){
                    badgeList.add(Badge.realBadgeBuilder()
                            .badgeName("긍정 뱃지")
                            .badgeNumber(9)
                            .member(BadgeOwner)
                            .createdAt(LocalDate.now())
                            .build());
                }
            }
            if(goalList.get(0).getHeartList().size() >= 10){
                if(BadgeOwner.getBadgeList().stream().noneMatch(b->b.getBadgeName().equals("매력 뱃지"))){
                    badgeList.add(Badge.realBadgeBuilder()
                            .badgeName("매력 뱃지")
                            .badgeNumber(10)
                            .member(BadgeOwner)
                            .createdAt(LocalDate.now())
                            .build());
                }
            }
            if(BadgeOwner.getBadgeList().size()>4){
                if(BadgeOwner.getBadgeList().stream().noneMatch(b->b.getBadgeName().equals("인싸 뱃지"))){
                    badgeList.add(Badge.realBadgeBuilder()
                            .badgeName("뱃지 왕 뱃지")
                            .member(BadgeOwner)
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
            goalList.get(0).getHeartList().forEach(this::delete);
            heartRepository.deleteAll(goalList.get(0).getHeartList());
            HeartResponseDto heartResponseDto = HeartResponseDto.builder()
                    .heartCnt(goalList.get(0).getHeartList().size())
                    .heartCheck(false)
                    .msgDto(GenerateMsg.getMsg(HttpServletResponse.SC_OK, "친구응원을 취소하셨습니다."))
                    .build();
            goalList.get(0).getMember().minusClickCnt();
            return ResponseEntity.ok().body(heartResponseDto);
        }
    }

    private void delete(Heart heart) {
        heart.getGoal().getHeartList().remove(heart);
    }

    private void createHeart(Goal goal, List<Heart> heartList, Principaldetail principaldetail) {
        heartList.add(Heart.builder().goal(goal).socialId(principaldetail.getMember().getSocialId()).build());
    }
}
