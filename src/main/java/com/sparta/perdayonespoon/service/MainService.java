package com.sparta.perdayonespoon.service;

import com.sparta.perdayonespoon.domain.Goal;
import com.sparta.perdayonespoon.domain.MsgCollector;
import com.sparta.perdayonespoon.domain.dto.CountDto;
import com.sparta.perdayonespoon.domain.dto.request.GoalDto;
import com.sparta.perdayonespoon.domain.dto.response.AchivementResponseDto;
import com.sparta.perdayonespoon.domain.dto.response.Goal.TodayGoalsDto;
import com.sparta.perdayonespoon.domain.dto.response.rate.GoalRateDto;
import com.sparta.perdayonespoon.domain.dto.response.Goal.GoalResponseDto;
import com.sparta.perdayonespoon.domain.dto.response.rate.WeekRateDto;
import com.sparta.perdayonespoon.jwt.Principaldetail;
import com.sparta.perdayonespoon.repository.GoalRepository;
import com.sparta.perdayonespoon.util.GenerateMsg;
import com.sparta.perdayonespoon.util.GetCharacterUrl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletResponse;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MainService {
    private static Stack<String> socialst = new Stack<>();
    private static Set<Integer> daylist = new HashSet<>();
    private static Stack<Boolean> goalst = new Stack<>();

    private static double truecount =0;
    private static double totalcount = 0;
    private static long period=0;
    private final GoalRepository goalRepository;
    public ResponseEntity getGoal(Principaldetail principaldetail) {
        LocalDateTime sunday;
        LocalDateTime saturday;
        int day = LocalDate.now().getDayOfWeek().getValue();
        List<GoalRateDto> goalRateDtos;
        if(day != 6 && day != 7) {
            sunday = LocalDateTime.now().minusDays(day);
            saturday = LocalDateTime.now().plusDays(6-day);
            goalRateDtos = goalRepository.getRateGoal(sunday,saturday,principaldetail.getMember().getSocialId());
            goalRateDtos.stream().sorted(Comparator.comparing(GoalRateDto::getDayString).thenComparing(GoalRateDto::isCheckGoal)).forEach(this::setRate);
        }else if(day == 6){
            sunday = LocalDateTime.now().minusDays(day);
            saturday = LocalDateTime.now();
            goalRateDtos = goalRepository.getRateGoal(sunday,saturday,principaldetail.getMember().getSocialId());
            goalRateDtos.stream().sorted(Comparator.comparing(GoalRateDto::getDayString).thenComparing(GoalRateDto::isCheckGoal)).forEach(this::setRate);
        }else {
            sunday = LocalDateTime.now();
            saturday = LocalDateTime.now().plusDays(6);
            goalRateDtos = goalRepository.getRateGoal(sunday,saturday,principaldetail.getMember().getSocialId());
            goalRateDtos.stream().sorted(Comparator.comparing(GoalRateDto::getDayString).thenComparing(GoalRateDto::isCheckGoal)).forEach(this::setRate);
        }
        if(!socialst.isEmpty() && !goalst.isEmpty()){
            socialst.clear();
            goalst.clear();
        }
        List<WeekRateDto> weekRateDtoList ;
        weekRateDtoList = goalRateDtos.stream().filter(this::checkgoalgetday).map(GoalRateDto::getWeekRateDto).collect(Collectors.toList());
        if(weekRateDtoList.isEmpty())
            weekRateDtoList = new ArrayList<>();
        for(int y=1; y<=7; y++){
            if(!daylist.isEmpty()) {
                if (!daylist.contains(y)) {
                    if(y == 7){
                        weekRateDtoList.add(0,WeekRateDto.builder().id(0).rate(0).dayString(DayOfWeek.of(y).getDisplayName(TextStyle.SHORT, Locale.KOREAN)).build());
                    }
                    else weekRateDtoList.add(y-1,WeekRateDto.builder().id(y).rate(0).dayString(DayOfWeek.of(y).getDisplayName(TextStyle.SHORT, Locale.KOREAN)).build());
                }
            }
            else {
                if(y == 7){
                    weekRateDtoList.add(0,WeekRateDto.builder().rate(0).id(0).dayString(DayOfWeek.of(y).getDisplayName(TextStyle.SHORT, Locale.KOREAN)).build());
                }
                else weekRateDtoList.add(WeekRateDto.builder().rate(0).id(y).dayString(DayOfWeek.of(y).getDisplayName(TextStyle.SHORT, Locale.KOREAN)).build());}
        }

        if(!daylist.isEmpty()){
            daylist.clear();
        }

        // 레디스랑 sse SMTP -> 회원가입했을때 메일보내주는거
        List<TodayGoalsDto> todayGoalsDtoList = goalRepository.getTodayGoal(LocalDateTime.now(),principaldetail.getMember().getSocialId());
        AchivementResponseDto achivementResponseDto = AchivementResponseDto.builder()
                .weekRateDtoList(weekRateDtoList)
                .todayGoalsDtoList(todayGoalsDtoList)
                .msgDto(GenerateMsg.getMsg(HttpServletResponse.SC_OK,"주간 습관 확인에 성공하셨습니다. 힘내세요!"))
                .weekStartDate(sunday.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")).substring(0,13))
                .weekEndDate(saturday.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")).substring(0,13))
                .currentDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")).substring(0,13))
                .build();
        return ResponseEntity.ok(achivementResponseDto);
    }
    private boolean checkgoalgetday(GoalRateDto goalRateDto){
        if(goalRateDto.isCheckGoal()){
            daylist.add(goalRateDto.getWhatsDay());
            return true;
        }
        return false;
    }
    //Todo: true false가 다 존재할땐 기능하지만 개별적으로 존재할때 기능이 동작할지 의문?
    private void setRate(GoalRateDto goalRateDto) {
        if (socialst.isEmpty() && goalst.isEmpty()) {
            socialst.push(goalRateDto.getDayString());
            goalst.push(goalRateDto.isCheckGoal());
            totalcount = goalRateDto.getTotalcount();
            if (goalRateDto.isCheckGoal()) {
                truecount = goalRateDto.getTotalcount();
                goalRateDto.setTotalcount((long) totalcount);
                goalRateDto.setRate(Math.round((truecount / totalcount) * 100));
            }
        } else if (socialst.peek().equals(goalRateDto.getDayString()) && goalst.peek() == !goalRateDto.isCheckGoal()) {
            socialst.pop();
            goalst.pop();
            totalcount += goalRateDto.getTotalcount();
            if (goalRateDto.isCheckGoal()) {
                truecount = goalRateDto.getTotalcount();
                goalRateDto.setTotalcount((long) totalcount);
                goalRateDto.setRate(Math.round((truecount / totalcount) * 100));
            }
        } else if (!socialst.peek().equals(goalRateDto.getDayString())) {
            socialst.pop();
            goalst.pop();
            socialst.add(goalRateDto.getDayString());
            goalst.add(goalRateDto.isCheckGoal());
            totalcount = goalRateDto.getTotalcount();
            if (goalRateDto.isCheckGoal()) {
                truecount = goalRateDto.getTotalcount();
                goalRateDto.setTotalcount((long) totalcount);
                goalRateDto.setRate(Math.round((truecount / totalcount) * 100));
            }
        }
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
        if(checkdate(LocalTime.parse(goalDto.time),goalDto.category,principaldetail.getMember().getSocialId())){
            while(period -->0){
                    goalList.add(Goal.builder()
                    .achievementCheck(goalDto.achievementCheck)
                    .startDate(LocalDateTime.now())
                    .currentDate(LocalDateTime.now().plusDays(x))
                    .endDate(LocalDateTime.now().plusDays(goalDto.category))
                    .privateCheck(goalDto.privateCheck)
                    .socialId(principaldetail.getMember().getSocialId())
                    .time(goalDto.time + ":00")
                    .category(goalDto.category)
                    .characterId(goalDto.characterId)
                    .title(goalDto.title)
                    .build());
                x++;
            }
            goalRepository.saveAll(goalList);
            List<GoalResponseDto> goalResponseDtoList = new ArrayList<>();
            goalList.forEach(Goal -> goalResponseDtoList.add(GoalResponseDto.builder()
                    .currentdate(Goal.getCurrentDate().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")).substring(0,13))
                    .achievementCheck(Goal.isAchievementCheck())
                    .id(Goal.getId())
                    .socialId(Goal.getSocialId())
                    .title(Goal.getTitle())
                    .characterUrl(GetCharacterUrl.getMandooUrl(Goal.getCharacterId()))
                    .endDate(Goal.getEndDate().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")).substring(0,13))
                    .startDate(Goal.getStartDate().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")).substring(0,13))
                    .privateCheck(Goal.isPrivateCheck())
                    .time(Goal.getTime())
                    .msgDto(GenerateMsg.getMsg(MsgCollector.CREATE_GOALS.getCode(), MsgCollector.CREATE_GOALS.getMsg()))
                    .build()));
            return ResponseEntity.ok(goalResponseDtoList);
        }
            throw new IllegalArgumentException("하루에 최대 5개까지만 습관 생성이 가능합니다.");
    }

    private boolean checkdate (LocalTime time, long category,String socialId){
        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDateTime endDate = localDateTime.plusDays(category);
        period = Period.between(localDateTime.toLocalDate(),endDate.toLocalDate()).getDays()+1;
        Optional<CountDto> countDto = goalRepository.getCountGoal(localDateTime,socialId);
        if(countDto.isPresent()) {
            if (countDto.get().getTotalCount() >= 5) {
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
    public ResponseEntity<GoalResponseDto> ChangeGoal(long goalId,Boolean achivement,Principaldetail principaldetail) {
        if(achivement == null){
            throw new IllegalArgumentException("통신시 달성여부가 보내져야 합니다.");
        }
        return goalRepository.findByIdAndSocialId(goalId,principaldetail.getMember().getSocialId())
                .map(g -> changeCheckGoal(g,achivement))
                .orElseThrow(() -> new IllegalArgumentException("해당 습관이 존재하지 않습니다."));
    }
    private ResponseEntity<GoalResponseDto> changeCheckGoal(Goal goal,boolean achivement) {
        goal.SetAchivementCheck(achivement);
        goalRepository.save(goal);
        return ResponseEntity.ok(GoalResponseDto.builder()
                .currentdate(goal.getCurrentDate().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")).substring(0,13))
                .startDate(goal.getStartDate().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")).substring(0,13))
                .endDate(goal.getEndDate().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")).substring(0,13))
                .title(goal.getTitle())
                .achievementCheck(goal.isAchievementCheck())
                .id(goal.getId())
                .privateCheck(goal.isPrivateCheck())
                .msgDto(GenerateMsg.getMsg(HttpServletResponse.SC_OK,"습관 달성 축하드립니다.!!! 고생 많으셨어요"))
                .socialId(goal.getSocialId())
                .characterId(goal.getCharacterId())
                .time(goal.getTime())
                .build());
    }
}