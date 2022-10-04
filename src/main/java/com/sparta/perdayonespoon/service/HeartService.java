package com.sparta.perdayonespoon.service;

import com.sparta.perdayonespoon.domain.*;
import com.sparta.perdayonespoon.domain.dto.HeartResponseDto;
import com.sparta.perdayonespoon.domain.dto.response.MsgDto;
import com.sparta.perdayonespoon.jwt.Principaldetail;
import com.sparta.perdayonespoon.repository.BadgeRepository;
import com.sparta.perdayonespoon.repository.HeartRepository;
import com.sparta.perdayonespoon.repository.MemberRepository;
import com.sparta.perdayonespoon.sse.NotificationType;
import com.sparta.perdayonespoon.sse.service.NotificationService;
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
    private final NotificationService notificationService;
    private final BadgeRepository badgeRepository;
    private final HeartRepository heartRepository;

    private final MemberRepository memberRepository;


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
                    .msgDto(MsgDto.builder().code(HttpServletResponse.SC_OK).msg("친구를 응원하셨습니다.!!").build())
                    .build();

            List<Badge> badgeList = new ArrayList<>();
            member.addClickCnt();
            Member badgeOwner = goalList.get(0).getMember();
            if(member.getHeartClickCnt() >= 10){
                if(member.getBadgeList().stream().noneMatch(b->b.getBadgeName().equals("긍정 뱃지"))){
                    String message = "축하합니다! 🛫 긍정 뱃지를 획득하셨습니다.";
                    notificationService.send(BadgeSseDto.builder()
                            .notificationType(NotificationType.Badge)
                            .message(message)
                            .member(member)
                            .build());
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
                    String message = "축하합니다! 💘 매력 뱃지를 획득하셨습니다.";
                    notificationService.send(BadgeSseDto.builder()
                            .notificationType(NotificationType.Badge)
                            .message(message)
                            .member(badgeOwner)
                            .build());
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
                    String message = "축하합니다! 👑 뱃지 왕 뱃지를 획득하셨습니다.";
                    notificationService.send(BadgeSseDto.builder()
                            .notificationType(NotificationType.Badge)
                            .message(message)
                            .member(member)
                            .build());
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
                    String message = "축하합니다! 👑 뱃지 왕 뱃지를 획득하셨습니다.";
                    notificationService.send(BadgeSseDto.builder()
                            .notificationType(NotificationType.Badge)
                            .message(message)
                            .member(badgeOwner)
                            .build());
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
            if(goalList.get(0).getTitle().length() <= 8) {
                String message = goalList.get(0).getTitle()+"에 " + member.getNickname() + "님이 좋아요를 눌렀습니다!";
                notificationService.send(BadgeSseDto.builder()
                        .notificationType(NotificationType.Heart)
                        .message(message)
                        .member(goalList.get(0).getMember())
                        .build());
            }else {
                String message = goalList.get(0).getTitle().substring(0,8)+"...에 " + member.getNickname() + "님이 좋아요를 눌렀습니다!";
                notificationService.send(BadgeSseDto.builder()
                        .notificationType(NotificationType.Heart)
                        .message(message)
                        .member(goalList.get(0).getMember())
                        .build());
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
                    .msgDto(MsgDto.builder().code(HttpServletResponse.SC_OK).msg("친구응원을 취소하셨습니다.").build())
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
