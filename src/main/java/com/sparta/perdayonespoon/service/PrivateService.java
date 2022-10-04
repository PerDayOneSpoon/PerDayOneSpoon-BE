package com.sparta.perdayonespoon.service;

import com.sparta.perdayonespoon.domain.Badge;
import com.sparta.perdayonespoon.domain.BadgeSseDto;
import com.sparta.perdayonespoon.domain.Goal;
import com.sparta.perdayonespoon.domain.Member;
import com.sparta.perdayonespoon.domain.dto.request.PrivateDto;
import com.sparta.perdayonespoon.domain.dto.response.Goal.GoalResponseDto;
import com.sparta.perdayonespoon.domain.dto.response.MsgDto;
import com.sparta.perdayonespoon.jwt.Principaldetail;
import com.sparta.perdayonespoon.repository.BadgeRepository;
import com.sparta.perdayonespoon.repository.GoalRepository;
import com.sparta.perdayonespoon.sse.NotificationType;
import com.sparta.perdayonespoon.sse.service.NotificationService;
import com.sparta.perdayonespoon.util.GetCharacterUrl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrivateService {
    private final NotificationService notificationService;
    private final BadgeRepository badgeRepository;
    private final GoalRepository goalRepository;
    @Transactional
    public ResponseEntity changePrivateCheck(Principaldetail principaldetail, PrivateDto privateDto, String goalFlag) {
        List<Goal> goalList = goalRepository.getCategoryGoals(principaldetail.getMember().getSocialId(),goalFlag);
        List<GoalResponseDto> goalResponseDtoList = new ArrayList<>();
        if(goalList.isEmpty()) throw new IllegalArgumentException("해당 습관이 없습니다.");
        goalList.forEach(goal -> changePrivate(goal,privateDto.getPrivateCheck(),goalResponseDtoList));
        Member badgeOwner = goalList.get(0).getMember();
        List<Badge> badgeList = new ArrayList<>();
        if(badgeOwner.getBadgeList().stream().noneMatch(b->b.getBadgeName().equals("프라이빗 뱃지"))){
            List<String> privateBadgeCheckDtoList = badgeOwner.getGoalList().stream().filter(Goal::isPrivateCheck).map(Goal::getGoalFlag).distinct().collect(Collectors.toList());
            if (privateBadgeCheckDtoList.size() >= 10) {
                String message = "축하합니다! 🔏 프라이빗 뱃지를 획득하셨습니다.";
                notificationService.send(BadgeSseDto.builder()
                        .notificationType(NotificationType.Badge)
                        .message(message)
                        .member(badgeOwner)
                        .build());
                badgeList.add(Badge.realBadgeBuilder()
                        .badgeName("프라이빗 뱃지")
                        .member(badgeOwner)
                        .createdAt(LocalDate.now())
                        .badgeNumber(2)
                        .build());
            }
        }
        if(badgeOwner.getBadgeList().size()>=5){
            if(badgeOwner.getBadgeList().stream().noneMatch(badge -> badge.getBadgeName().equals("뱃지 왕 뱃지"))){
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
                        .build());}
        }
        if(!badgeList.isEmpty()){
            badgeRepository.saveAll(badgeList);
        }
        goalRepository.saveAll(goalList);
        return ResponseEntity.ok().body(goalResponseDtoList);
    }

    private void changePrivate(Goal goal,Boolean privateCheck,List<GoalResponseDto> goalResponseDtoList) {
        goal.SetPrivateCheck(Objects.requireNonNullElseGet(privateCheck, () -> !goal.isPrivateCheck()));
        if(goal.isPrivateCheck()) {
            goalResponseDtoList.add(GoalResponseDto
                    .builder()
                    .id(goal.getId())
                    .characterUrl(GetCharacterUrl.getMandooUrl(goal.getCharacterId()))
                    .achievementCheck(goal.isAchievementCheck())
                    .privateCheck(goal.isPrivateCheck())
                    .time(goal.getTime())
                    .startDate(goal.getStartDate().format((DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"))).substring(0, 13))
                    .endDate(goal.getEndDate().format((DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"))).substring(0, 13))
                    .currentdate(goal.getCurrentDate().format((DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"))).substring(0, 13))
                    .title(goal.getTitle())
                    .socialId(goal.getSocialId())
                    .msgDto(MsgDto.builder().code(HttpServletResponse.SC_OK).msg("나만보기로 설정하셨습니다.").build())
                    .build());
        }
        goalResponseDtoList.add(GoalResponseDto
                .builder()
                .id(goal.getId())
                .characterUrl(GetCharacterUrl.getMandooUrl(goal.getCharacterId()))
                .achievementCheck(goal.isAchievementCheck())
                .privateCheck(goal.isPrivateCheck())
                .time(goal.getTime())
                .startDate(goal.getStartDate().format((DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"))).substring(0, 13))
                .endDate(goal.getEndDate().format((DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"))).substring(0, 13))
                .currentdate(goal.getCurrentDate().format((DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"))).substring(0, 13))
                .title(goal.getTitle())
                .socialId(goal.getSocialId())
                .msgDto(MsgDto.builder().code(HttpServletResponse.SC_OK).msg("공개보기로 설정하셨습니다.").build())
                .build());
    }
}
