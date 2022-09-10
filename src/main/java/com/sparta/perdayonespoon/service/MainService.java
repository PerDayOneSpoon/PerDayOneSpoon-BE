package com.sparta.perdayonespoon.service;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sparta.perdayonespoon.domain.Goal;
import com.sparta.perdayonespoon.domain.MsgCollector;
import com.sparta.perdayonespoon.domain.dto.request.GoalDto;
import com.sparta.perdayonespoon.domain.dto.response.GoalRateDto;
import com.sparta.perdayonespoon.domain.dto.response.GoalResponseDto;
import com.sparta.perdayonespoon.jwt.Principaldetail;
import com.sparta.perdayonespoon.repository.GoalRepository;
import com.sparta.perdayonespoon.util.GenerateMsg;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.implementation.bytecode.Throw;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MainService {

    private static long i = 0;

    private static double totalcount = 0F;

    private static double truecount = 0F;

    private final GoalRepository goalRepository;
    public ResponseEntity getGoal(Principaldetail principaldetail) {
        DayOfWeek dayOfWeek = LocalDateTime.now().getDayOfWeek();
        System.out.println(dayOfWeek);
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
        switch ( goalRateDto.getWhatsday()){
            case 1 : goalRateDto.setDayString("월");
            break;
            case 2 : goalRateDto.setDayString("화");
            break;
            case 3 : goalRateDto.setDayString("수");
            break;
            case 4 : goalRateDto.setDayString("목");
            break;
            case 5 : goalRateDto.setDayString("금");
            break;
            case 6 : goalRateDto.setDayString("토");
            break;
            case 7 : goalRateDto.setDayString("일");
            break;
        }
        if(i != 1) {
            totalcount += goalRateDto.getTotalcount();
            if(goalRateDto.isCheckGoal()){
                truecount = goalRateDto.getTotalcount();
            }
            goalRateDto.setRate(Math.round((truecount/totalcount)*100));
            i++;
        } else if(i == 1){
            totalcount += goalRateDto.getTotalcount();
            if(goalRateDto.isCheckGoal()){
                truecount = goalRateDto.getTotalcount();
            }
            goalRateDto.setRate(truecount/totalcount);
            totalcount=0;
            truecount=0;
            i--;
        }
    }

    // TODO : 달력 날짜 받기X 주간 달성도 리턴하기
    public ResponseEntity CreateGoal(List<GoalDto> goalDto, Principaldetail principaldetail) {
        List<Goal> goalList = new ArrayList<>();
        goalDto.stream().filter(g->checkdate(LocalTime.parse(g.time))).forEach(GoalDto -> goalList.add(Goal.builder()
                .achievementCheck(GoalDto.achievementCheck)
                .socialId(principaldetail.getMember().getSocialId())
                .title(GoalDto.title)
                .characterId(GoalDto.characterId)
                .category(GoalDto.category)
                .start_date(LocalDateTime.now())
                .end_date(LocalDateTime.now().plusDays(GoalDto.getCategory()-1))
                .time(GoalDto.time)
                .privateCheck(GoalDto.isPrivateCheck())
                .build()));
        goalRepository.saveAll(goalList);
        List<GoalResponseDto> goalResponseDtoList = new ArrayList<>();
        goalList.forEach(Goal -> goalResponseDtoList.add(GoalResponseDto.builder()
                .achievementCheck(Goal.isAchievementCheck())
                .id(Goal.getId())
                .socialId(Goal.getSocialId())
                .title(Goal.getTitle())
                .category(Goal.getCategory())
                .characterId(Goal.getCharacterId())
                .end_date(Goal.getEnd_date().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .start_date(Goal.getStart_date().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .privateCheck(Goal.isPrivateCheck())
                .time(Goal.getTime())
                .msgDto(GenerateMsg.getMsg(MsgCollector.CREATE_GOALS.getCode(), MsgCollector.CREATE_GOALS.getMsg()))
                .build()));
        return ResponseEntity.ok(goalResponseDtoList);
    }
    private boolean checkdate (LocalTime time){
        LocalDateTime localDateTime = LocalDateTime.now();
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

    //        List<Goal> goalList = goalRepository.findAllBySocialId(principaldetail.getMember().getSocialId());
//        List<GoalResponseDto> goalResponseDtoList = new ArrayList<>();
//        goalList.forEach(Goal -> goalResponseDtoList.add(GoalResponseDto.builder()
//                .id(Goal.getId())
//                .socialId(Goal.getSocialId())
//                .title(Goal.getTitle())
//                .category(Goal.getCategory())
//                .characterId(Goal.getCharacterId())
//                .end_date(Goal.getEnd_date().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss")))
//                .start_date(Goal.getStart_date().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss")))
//                .privateCheck(Goal.isPrivateCheck())
//                .time(Goal.getTime())
//                .msgDto(GenerateMsg.getMsg(MsgCollector.FIND_GOALS.getCode(), MsgCollector.FIND_GOALS.getMsg()))
//                .build()));
}