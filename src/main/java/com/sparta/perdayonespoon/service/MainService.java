package com.sparta.perdayonespoon.service;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sparta.perdayonespoon.domain.Goal;
import com.sparta.perdayonespoon.domain.MsgCollector;
import com.sparta.perdayonespoon.domain.dto.CountDto;
import com.sparta.perdayonespoon.domain.dto.request.GoalDto;
import com.sparta.perdayonespoon.domain.dto.response.GoalRateDto;
import com.sparta.perdayonespoon.domain.dto.response.GoalResponseDto;
import com.sparta.perdayonespoon.jwt.Principaldetail;
import com.sparta.perdayonespoon.repository.GoalRepository;
import com.sparta.perdayonespoon.util.GenerateMsg;
import com.sparta.perdayonespoon.util.Time;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.implementation.bytecode.Throw;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MainService {

    private static Stack<Integer> socialst = new Stack<>();

    private static Stack<Boolean> goalst = new Stack<>();

    private static long i = 0;

    private static double totalcount = 0F;

    private static double truecount = 0F;

    private static long period=0;

    private final GoalRepository goalRepository;
    public ResponseEntity getGoal(Principaldetail principaldetail) {
        int day = LocalDate.now().getDayOfWeek().getValue();
        List<GoalRateDto> goalRateDtos;
        if(day != 6 && day != 7) {
            LocalDateTime sunday = LocalDateTime.now().minusDays(day);
            LocalDateTime saturday = LocalDateTime.now().plusDays(6-day);
            goalRateDtos = goalRepository.getRateGoal(sunday,saturday,principaldetail.getMember().getSocialId());
            goalRateDtos.forEach(this::setRate);
        }else if(day == 6){
            LocalDateTime sunday = LocalDateTime.now().minusDays(day);
            LocalDateTime saturday = LocalDateTime.now();
            goalRateDtos = goalRepository.getRateGoal(sunday,saturday,principaldetail.getMember().getSocialId());
            goalRateDtos.forEach(this::setRate);
        }else {
            LocalDateTime sunday = LocalDateTime.now();
            LocalDateTime saturday = LocalDateTime.now().plusDays(6);
            goalRateDtos = goalRepository.getRateGoal(sunday,saturday,principaldetail.getMember().getSocialId());
            goalRateDtos.forEach(this::setRate);
        }
        List<GoalRateDto> goalRateDtoList = goalRateDtos.stream().filter(GoalRateDto::isCheckGoal).collect(Collectors.toList());
        return ResponseEntity.ok(goalRateDtoList);
    }

    // 0 2 4 6 8 10 완료한 개수
    // 1 3 5 7 9 11 실패한 개수
    // 0+1 전체개수 0 완료한개수
    private void setRate(GoalRateDto goalRateDto) {
        goalRateDto.SetTwoField(GenerateMsg.getMsg(HttpServletResponse.SC_OK,"주간 통계 조회에 성공하셨습니다."));
        if(socialst.isEmpty() && goalst.isEmpty()) {
            socialst.push(0);
            goalst.push(goalRateDto.isCheckGoal());
            totalcount = goalRateDto.getTotalcount();
            if(goalRateDto.isCheckGoal()) {
                truecount = goalRateDto.getTotalcount();
                goalRateDto.setRate(Math.round((truecount/totalcount)*100));
            }
        }
        else if(socialst.peek() ==0 && goalst.peek() == !goalRateDto.isCheckGoal()){
            socialst.pop();
            goalst.pop();
            totalcount += goalRateDto.getTotalcount();
            goalRateDto.setRate(Math.round((truecount/totalcount)*100));
        } else if(socialst.peek() == 0 && goalst.peek() == goalRateDto.isCheckGoal()){
            socialst.pop();
            goalst.pop();
            totalcount = goalRateDto.getTotalcount();
            if(goalRateDto.isCheckGoal()){
                truecount = goalRateDto.getTotalcount();
                goalRateDto.setRate(Math.round((truecount/totalcount)*100));
            }
        }
//        if(goalRateDto.isCheckGoal()){
//            truecount = goalRateDto.getTotalcount();
//            goalRateDto.setRate(Math.round((truecount/totalcount)*100));
//            totalcount =0;
//            truecount =0;
//        }
//        if(i != 1) {
//            totalcount += goalRateDto.getTotalcount();
//            if(goalRateDto.isCheckGoal()){
//                truecount = goalRateDto.getTotalcount();
//                goalRateDto.setRate(Math.round((truecount/totalcount)*100));
//                totalcount=0;
//                truecount=0;
//            }
//            i++;
//        } else if(i == 1){
//            totalcount += goalRateDto.getTotalcount();
//            if(goalRateDto.isCheckGoal()){
//                truecount = goalRateDto.getTotalcount();
//                goalRateDto.setRate(Math.round((truecount/totalcount)*100));
//                totalcount=0;
//                truecount=0;
//            }
//            i--;
//        }
    }

    // TODO : 달력 날짜 받기X 주간 달성도 리턴하기
    public ResponseEntity CreateGoal(GoalDto goalDto, Principaldetail principaldetail) {
        if(goalDto.getTitle() == null){
            throw new IllegalArgumentException("제목을 입력해주세요");
        } else if (goalDto.getCharacterId() == 0){
            throw new IllegalArgumentException("캐릭터를 선택해 주세요");
        }
        int x=0;
        List<Goal> goalList = new ArrayList<>();
        if(checkdate(LocalTime.parse(goalDto.time),goalDto.category)){
            while(period -->0){
                goalList.add(Goal.builder()
                        .achievementCheck(goalDto.achievementCheck)
                        .start_date(LocalDateTime.now())
                        .currentdate(LocalDateTime.now().plusDays(x))
                        .end_date(LocalDateTime.now().plusDays(goalDto.category))
                        .privateCheck(goalDto.privateCheck)
                        .socialId(principaldetail.getMember().getSocialId())
                        .time(goalDto.time)
                        .category(goalDto.category)
                        .characterId(goalDto.characterId)
                        .title(goalDto.title)
                        .build());
                x++;
            }
            goalRepository.saveAll(goalList);
            List<GoalResponseDto> goalResponseDtoList = new ArrayList<>();
            goalList.forEach(Goal -> goalResponseDtoList.add(GoalResponseDto.builder()
                    .currentdate(Goal.getCurrentdate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")).substring(0,10))
                    .achievementCheck(Goal.isAchievementCheck())
                    .id(Goal.getId())
                    .socialId(Goal.getSocialId())
                    .title(Goal.getTitle())
                    .category(Goal.getCategory())
                    .characterId(Goal.getCharacterId())
                    .end_date(Goal.getEnd_date().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")).substring(0,10))
                    .start_date(Goal.getStart_date().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")).substring(0,10))
                    .privateCheck(Goal.isPrivateCheck())
                    .time(Goal.getTime())
                    .msgDto(GenerateMsg.getMsg(MsgCollector.CREATE_GOALS.getCode(), MsgCollector.CREATE_GOALS.getMsg()))
                    .build()));
            return ResponseEntity.ok(goalResponseDtoList);
        }
            throw new IllegalArgumentException("하루에 최대 5개까지만 습관 생성이 가능합니다.");
    }

    private boolean checkdate (LocalTime time, long category){
        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDateTime end_date = localDateTime.plusDays(category);
        period = Period.between(localDateTime.toLocalDate(),end_date.toLocalDate()).getDays()+1;
        Optional<CountDto> countDto = goalRepository.getCountGoal(localDateTime);
        if(countDto.isPresent()) {
            if (countDto.get().getTotalcount() >= 5) {
                throw new IllegalArgumentException("하루의 습관은 최대 5개까지만 가능합니다. 다시 확인해주세요");
            }
        }
        if(time.getHour() == 0 && time.getMinute() == 0){
            throw new IllegalArgumentException("설정한 습관의 타이머를 유효한 값으로 수정해주세요");
        }
        if(localDateTime.getDayOfMonth() == localDateTime.plusHours(time.getHour()).getDayOfMonth()) {
            LocalDateTime localDateTime1 = localDateTime.plusHours(time.getHour());
            if (localDateTime.getDayOfMonth() == localDateTime1.plusMinutes(time.getMinute()).getDayOfMonth()) {
                return true;
            } else
                throw new IllegalArgumentException("금일을 넘는 목표는 생성할 수 없습니다. 다시 생성해 주세요");
        }
        else
            throw new IllegalArgumentException("금일을 넘는 목표는 생성할 수 없습니다. 다시 생성해 주세요");
    }
}