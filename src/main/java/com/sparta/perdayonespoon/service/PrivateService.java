package com.sparta.perdayonespoon.service;

import com.sparta.perdayonespoon.domain.Goal;
import com.sparta.perdayonespoon.domain.dto.request.PrivateDto;
import com.sparta.perdayonespoon.domain.dto.response.Goal.GoalResponseDto;
import com.sparta.perdayonespoon.jwt.Principaldetail;
import com.sparta.perdayonespoon.repository.GoalRepository;
import com.sparta.perdayonespoon.util.GenerateMsg;
import com.sparta.perdayonespoon.util.GetCharacterUrl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrivateService {
    private final GoalRepository goalRepository;
    public ResponseEntity changePrivateCheck(Principaldetail principaldetail, PrivateDto privateDto, String goalFlag) {
        List<Goal> goalList = goalRepository.getCategoryGoals(principaldetail.getMember().getSocialId(),goalFlag);
        List<GoalResponseDto> goalResponseDtoList = new ArrayList<>();
        if(goalList.isEmpty()) throw new IllegalArgumentException("해당 습관이 없습니다.");
        goalList.forEach(goal -> changePrivate(goal,privateDto.getPrivateCheck(),goalResponseDtoList));
        return ResponseEntity.ok().body(goalResponseDtoList);
    }

    private void changePrivate(Goal goal,Boolean privateCheck,List<GoalResponseDto> goalResponseDtoList) {
        if(privateCheck == null){
            goal.SetPrivateCheck(!goal.isPrivateCheck());
        }
        else{
            goal.SetPrivateCheck(privateCheck);
        }
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
                    .msgDto(GenerateMsg.getMsg(HttpServletResponse.SC_OK, "나만보기로 설정하셨습니다."))
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
                .msgDto(GenerateMsg.getMsg(HttpServletResponse.SC_OK, "공개보기로 설정하셨습니다."))
                .build());
        goalRepository.save(goal);
    }
}
