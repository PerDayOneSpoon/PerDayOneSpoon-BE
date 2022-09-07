package com.sparta.perdayonespoon.service;

import com.sparta.perdayonespoon.domain.Goal;
import com.sparta.perdayonespoon.domain.dto.request.GoalDto;
import com.sparta.perdayonespoon.domain.dto.response.GoalResponseDto;
import com.sparta.perdayonespoon.jwt.Principaldetail;
import com.sparta.perdayonespoon.repository.GoalRepository;
import com.sparta.perdayonespoon.util.GenerateMsg;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MainService {

    private final GoalRepository goalRepository;
    public ResponseEntity getGoal(Principaldetail principaldetail) {
        Calendar calendar = Calendar.getInstance(); // 현재시각 뽑는거같음
        List<Goal> goalList = goalRepository.findAllBySocialId(principaldetail.getMember().getSocialId());
        List<GoalResponseDto> goalResponseDtoList = new ArrayList<>();
        goalList.forEach(Goal -> goalResponseDtoList.add(GoalResponseDto.builder()
                        .id(Goal.getId())
                        .socialId(Goal.getSocialId())
                        .time(Goal.getTime())
                        .title(Goal.getTitle())
                        .privateCheck(Goal.isPrivateCheck())
                        .start_date(calendar.getTime().toString())
                        .end_date(Goal.getEnd_date())
                        .characterId(Goal.getCharacterId())
                        .category(Goal.getCategory())
                        .build()));
        goalResponseDtoList.forEach(this::SetTwoField);
        return ResponseEntity.ok(goalResponseDtoList);
    }
    private void SetTwoField(GoalResponseDto goalResponseDto){
        goalResponseDto.SetTwoProperties(GenerateMsg.getMsg(HttpServletResponse.SC_OK,"목표 확인에 성공하셨습니다."));
    }
    // TODO : 달력 날짜 받기X 주간 달성도 리턴하기
    public ResponseEntity CreateGoal(List<GoalDto> goalDto, Principaldetail principaldetail) {
        Calendar calendar = Calendar.getInstance(); // 현재시각 뽑는거같음
        List<Goal> goalList = new ArrayList<>();
        goalDto.forEach(GoalDto -> goalList.add(Goal.builder()
                .socialId(principaldetail.getMember().getSocialId())
                .title(GoalDto.title)
                .characterId(GoalDto.characterId)
                .category(GoalDto.category)
                .end_date(GoalDto.End_date)
                .start_date(GoalDto.Start_date)
                .time(GoalDto.time)
                .privateCheck(GoalDto.isPrivateCheck())
                .build()));
        goalRepository.saveAll(goalList);
        List<GoalResponseDto> goalResponseDtoList = new ArrayList<>();
        goalList.forEach(Goal -> goalResponseDtoList.add(GoalResponseDto.builder()
                .socialId(Goal.getSocialId())
                .title(Goal.getTitle())
                .category(Goal.getCategory())
                .characterId(Goal.getCharacterId())
                .end_date(Goal.getEnd_date())
                .start_date(Goal.getStart_date())
                .privateCheck(Goal.isPrivateCheck())
                .time(Goal.getTime())
                .msgDto(GenerateMsg.getMsg(HttpServletResponse.SC_OK,"목표 생성에 성공하셨습니다. 응원합니다"))
                .build()));
//        Goal goal = Goal.builder()
//                .title(goalDto.title)
//                .characterId(goalDto.CharacterId)
//                .category(goalDto.category)
//                .enddate(goalDto.EndDate)
//                .startdate(goalDto.StartDate)
//                .time(goalDto.time)
//                .build();
//        goalRepository.save(goal);
//        GoalResponseDto goalResponseDto = GoalResponseDto.builder()
//                .title(goal.getTitle())
//                .category(goal.getCategory())
//                .characterId(goal.getCharacterId())
//                .endDate(goal.getEnddate())
//                .startDate(goal.getStartdate())
//                .privatecheck(goal.isPrivateCheck())
//                .time(goal.getTime())
//                .build();
//        goalResponseDto.SetTwoProperties(GenerateMsg.getMsg(HttpServletResponse.SC_OK,"목표 생성에 성공하셨습니다. 응원합니다"));
        return ResponseEntity.ok(goalResponseDtoList);
    }
}
