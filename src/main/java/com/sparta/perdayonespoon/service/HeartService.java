package com.sparta.perdayonespoon.service;

import com.sparta.perdayonespoon.domain.Goal;
import com.sparta.perdayonespoon.domain.Heart;
import com.sparta.perdayonespoon.domain.dto.HeartResponseDto;
import com.sparta.perdayonespoon.domain.dto.response.Goal.GoalsAndHeart;
import com.sparta.perdayonespoon.jwt.Principaldetail;
import com.sparta.perdayonespoon.repository.GoalRepository;
import com.sparta.perdayonespoon.repository.HeartRepository;
import com.sparta.perdayonespoon.util.GenerateMsg;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class HeartService {
    private final HeartRepository heartRepository;

    private Goal goalCheck(Goal goal, Principaldetail principaldetail) {
        if(goal.getSocialId().equals(principaldetail.getMember().getSocialId()))
            throw new IllegalArgumentException("자신의 습관에 좋아요를 누를 수 없습니다.");
        else
            return goal;
    }

    public ResponseEntity<HeartResponseDto> addHearts(Principaldetail principaldetail, String goalFlag) {
        GoalsAndHeart goalsAndHearts = heartRepository.findGoalsHeart(goalFlag,principaldetail.getMember().getSocialId());
        if(!goalsAndHearts.getGoalList().isEmpty()) {
            if (goalsAndHearts.getGoalList().get(0).getSocialId().equals(principaldetail.getMember().getSocialId()))
                throw new IllegalArgumentException("자신의 습관에 좋아요를 할 수 없습니다");
        }
        else
            {throw new IllegalArgumentException("해당 습관이 존재하지 않습니다.");}

        if(goalsAndHearts.getHeartList().isEmpty()){
            List<Heart> newHeartList = new ArrayList<>();
            goalsAndHearts.getGoalList().forEach(goal-> createHeart(goal,newHeartList,principaldetail));
            heartRepository.saveAll(newHeartList);
            HeartResponseDto heartResponseDto = HeartResponseDto.builder()
                    .heartCnt(goalsAndHearts.getGoalList().get(0).getHeartList().size())
                    .heartCheck(true)
                    .msgDto(GenerateMsg.getMsg(HttpServletResponse.SC_OK,"친구를 응원하셨습니다.!!"))
                    .build();
            return ResponseEntity.ok().body(heartResponseDto);
        }else{
            heartRepository.deleteAll(goalsAndHearts.getHeartList());
            HeartResponseDto heartResponseDto = HeartResponseDto.builder()
                    .heartCnt(goalsAndHearts.getGoalList().get(0).getHeartList().size())
                    .heartCheck(false)
                    .msgDto(GenerateMsg.getMsg(HttpServletResponse.SC_OK, "친구응원을 취소하셨습니다."))
                    .build();
            return ResponseEntity.ok().body(heartResponseDto);
        }
    }
    private void createHeart(Goal goal, List<Heart> heartList, Principaldetail principaldetail) {
        heartList.add(Heart.builder().goal(goal).socialId(principaldetail.getMember().getSocialId()).build());
    }
}
