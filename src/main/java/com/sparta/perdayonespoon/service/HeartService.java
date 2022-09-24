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

    private final GoalRepository goalRepository;
    private final HeartRepository heartRepository;
    public ResponseEntity<HeartResponseDto> addHeart(Principaldetail principaldetail, Long goalId) {
        Queue<String> queue = new LinkedList<>();
        Goal goal = goalRepository.findById(goalId)
                .map(Goal->goalCheck(Goal,principaldetail))
                .orElseThrow(() -> new IllegalArgumentException("해당 습관이 존재하지 않습니다."));
        Heart heart = (Heart) heartRepository.findBySocialId(principaldetail.getMember().getSocialId())
                .map(Heart -> deleteHeart(Heart, queue))
                .orElseGet(() -> Heart.builder().goal(goal).socialId(principaldetail.getMember().getSocialId()).build());
        if(queue.isEmpty()){
            heartRepository.save(heart);
            HeartResponseDto heartResponseDto = HeartResponseDto.builder()
                    .heartCheck(true)
                    .msgDto(GenerateMsg.getMsg(HttpServletResponse.SC_OK,"친구를 응원하셨습니다.!!"))
                    .build();
            return ResponseEntity.ok().body(heartResponseDto);
        }
        else {
            HeartResponseDto heartResponseDto = HeartResponseDto.builder()
                    .heartCheck(false)
                    .msgDto(GenerateMsg.getMsg(HttpServletResponse.SC_OK, "친구응원을 취소하셨습니다."))
                    .build();
            return ResponseEntity.ok().body(heartResponseDto);
        }
    }
    private <U> U deleteHeart(Heart heart, Queue<String> queue) {
        queue.offer(heart.getSocialId());
        heartRepository.delete(heart);
        return null;
    }
    private Goal goalCheck(Goal goal, Principaldetail principaldetail) {
        if(goal.getSocialId().equals(principaldetail.getMember().getSocialId()))
            throw new IllegalArgumentException("자신의 습관에 좋아요를 누를 수 없습니다.");
        else
            return goal;
    }

    public ResponseEntity addHearts(Principaldetail principaldetail, String goalFlag) {
        List<GoalsAndHeart> goalsAndHearts = heartRepository.findGoalsHeart(goalFlag);
        List<Heart> heartList;
        heartList = goalsAndHearts.stream().map(GoalsAndHeart::getHeart).collect(Collectors.toList());
        if(heartList.isEmpty()){
            List<Goal> goalList = goalsAndHearts.stream().map(GoalsAndHeart::getGoal).collect(Collectors.toList());
            goalList.forEach(goal-> createHeart(goal,heartList,principaldetail));
            heartRepository.saveAll(heartList);
            HeartResponseDto heartResponseDto = HeartResponseDto.builder()
                    .heartCheck(true)
                    .msgDto(GenerateMsg.getMsg(HttpServletResponse.SC_OK,"친구를 응원하셨습니다.!!"))
                    .build();
            return ResponseEntity.ok().body(heartResponseDto);
        }else{
            heartRepository.deleteAll(heartList);
            HeartResponseDto heartResponseDto = HeartResponseDto.builder()
                    .heartCheck(false)
                    .msgDto(GenerateMsg.getMsg(HttpServletResponse.SC_OK, "친구응원을 취소하셨습니다."))
                    .build();
            return ResponseEntity.ok().body(heartResponseDto);
        }
//        if(presenceHeart.isEmpty()){
//            heartRepository.saveAll(heartList);
//            HeartResponseDto heartResponseDto = HeartResponseDto.builder()
//                    .heartCheck(true)
//                    .msgDto(GenerateMsg.getMsg(HttpServletResponse.SC_OK,"친구를 응원하셨습니다.!!"))
//                    .build();
//            return ResponseEntity.ok().body(heartResponseDto);
//        }
//        else {
//            HeartResponseDto heartResponseDto = HeartResponseDto.builder()
//                    .heartCheck(false)
//                    .msgDto(GenerateMsg.getMsg(HttpServletResponse.SC_OK, "친구응원을 취소하셨습니다."))
//                    .build();
//            return ResponseEntity.ok().body(heartResponseDto);
//        }
    }
    private void createHeart(Goal goal, List<Heart> heartList, Principaldetail principaldetail) {
        heartList.add(Heart.builder().goal(goal).socialId(principaldetail.getMember().getSocialId()).build());
    }
}
