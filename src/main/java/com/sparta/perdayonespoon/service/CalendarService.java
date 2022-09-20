package com.sparta.perdayonespoon.service;
import com.sparta.perdayonespoon.domain.Member;
import com.sparta.perdayonespoon.domain.dto.request.CalenderRequestDto;
import com.sparta.perdayonespoon.domain.dto.response.Goal.DayGoalsDto;
import com.sparta.perdayonespoon.domain.dto.response.Goal.TodayGoalsDto;
import com.sparta.perdayonespoon.domain.dto.response.calendar.CalendarFriendUniteDto;
import com.sparta.perdayonespoon.domain.dto.response.calendar.CalendarGoalsDto;
import com.sparta.perdayonespoon.domain.dto.response.calendar.CalendarUniteDto;
import com.sparta.perdayonespoon.domain.dto.response.calendar.MonthCalendarDto;
import com.sparta.perdayonespoon.domain.follow.FriendDto;
import com.sparta.perdayonespoon.jwt.Principaldetail;
import com.sparta.perdayonespoon.repository.FriendRepository;
import com.sparta.perdayonespoon.repository.GoalRepository;
import com.sparta.perdayonespoon.repository.MemberRepository;
import com.sparta.perdayonespoon.util.GenerateMsg;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CalendarService {

    private final MemberRepository memberRepository;

    private final FriendRepository friendRepository;

    private final GoalRepository goalRepository;

    //TODO : 여기는 캘린더를 들어왔을때 모든걸 보여주는 함수
    public ResponseEntity getAlldate(Principaldetail principaldetail) {
        HashMap<String, List<String>> twolist = new LinkedHashMap<>();
        List<MonthCalendarDto> monthGoalsDtoList = new ArrayList<>();
        Stack<String> dayCheck = new Stack<>();
        Stack<Long> id = new Stack<>();
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endDate = today.with(TemporalAdjusters.lastDayOfMonth());
        List<CalendarGoalsDto> calendarGoalsDtoList = goalRepository.getMyCalendar(startDate, endDate,principaldetail.getMember().getSocialId());
        calendarGoalsDtoList.forEach(calendarGoalsDto->CollectSameDate(calendarGoalsDto,twolist,dayCheck,monthGoalsDtoList,id));
        List<TodayGoalsDto> todayGoalsDtoList = calendarGoalsDtoList.stream().filter(c->c.getCurrentDate().equals(today.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")).substring(0,13)))
                .map(CalendarGoalsDto::getTodayGoalsDto).filter(g->checkMe(g,principaldetail.getMember().getId())).collect(Collectors.toList());
        if(!dayCheck.isEmpty()) {
            monthGoalsDtoList.add(MonthCalendarDto.builder().id(id.pop()).currentDate(dayCheck.peek()).charactorColorlist(twolist.get(dayCheck.pop())).build());
        }
        List<FriendDto> peopleList = friendRepository.getFollowerList(principaldetail.getMember().getSocialId());
        Member myMember = memberRepository.findBySocialId(principaldetail.getMember().getSocialId()).orElseThrow(() ->new IllegalArgumentException("해당 유저가 없습니다"));
        peopleList.add(0,FriendDto.builder().id(myMember.getId()).nickname(myMember.getNickname()).profileImage(myMember.getImage().getImgUrl()).build());
        CalendarUniteDto calenderUniteDto = CalendarUniteDto.builder().startDate(startDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")))
                .endDate(endDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")))
                .monthCalenderDtoList(monthGoalsDtoList)
                .todayGoalsDtoList(todayGoalsDtoList)
                .peopleList(peopleList)
                .isMe(true)
                .msgDto(GenerateMsg.getMsg(HttpServletResponse.SC_OK,"금일 캘린더 조회에 성공하셨습니다.!"))
                .build();
        return ResponseEntity.ok().body(calenderUniteDto);
    }

    private void CollectSameDate(CalendarGoalsDto calenderGoalsDtoList,Map<String,List<String>> twolist, Stack<String> daycheck, List<MonthCalendarDto> monthCalenderDtoList,Stack<Long> id) {
        if(twolist.containsKey(calenderGoalsDtoList.getCurrentDate())) {
            twolist.get(calenderGoalsDtoList.getCurrentDate()).add(calenderGoalsDtoList.getCharactorColor());
        }
        else {
            if(daycheck.isEmpty()) {
                id.push(calenderGoalsDtoList.getId());
                daycheck.push(calenderGoalsDtoList.getCurrentDate());
                List<String> charactorColors = new ArrayList<>();
                charactorColors.add(calenderGoalsDtoList.getCharactorColor());
                twolist.put(calenderGoalsDtoList.getCurrentDate(), charactorColors);
            }
            else
            {
                monthCalenderDtoList.add(MonthCalendarDto.builder().id(id.pop()).currentDate(daycheck.peek()).charactorColorlist(twolist.get(daycheck.pop())).build());
                daycheck.push(calenderGoalsDtoList.getCurrentDate());
                id.push(calenderGoalsDtoList.getId());
                List<String> charactorColors = new ArrayList<>();
                charactorColors.add(calenderGoalsDtoList.getCharactorColor());
                twolist.put(calenderGoalsDtoList.getCurrentDate(), charactorColors);
            }
        }
    }

    //TODO :  캘린더에서 특정 날짜 눌러서 데이터 나오는거 통합 api 적용중
    public ResponseEntity findMemberSpecificDate(CalenderRequestDto calenderRequestDto, Principaldetail principaldetail) {
        assert calenderRequestDto.getCalenderDate() != null;
        boolean isMe = false;
        LocalDate localDate = LocalDate.parse(calenderRequestDto.getCalenderDate());
        LocalDateTime localDateTime = LocalDateTime.of(localDate.getYear(), localDate.getMonth(), localDate.getDayOfMonth(), 0, 0, 0);
        List<TodayGoalsDto> todayGoalsDtoList;
        if(Objects.equals(calenderRequestDto.getMemberId(), principaldetail.getMember().getId())) {
            todayGoalsDtoList = goalRepository.getTodayGoal(localDateTime, principaldetail.getMember().getSocialId());
        } else {
            todayGoalsDtoList = goalRepository.getFriendTodayGoal(localDateTime, calenderRequestDto.getMemberId(), false);
        }
        if(Objects.equals(calenderRequestDto.getMemberId(), principaldetail.getMember().getId())) isMe = true;
        DayGoalsDto dayGoalsDto = DayGoalsDto.builder().todayGoalsDtoList(todayGoalsDtoList).isMe(isMe).build();
        return ResponseEntity.ok().body(dayGoalsDto);
    }

    //TODO :  캘린더에서 특정 날짜 눌러서 데이터 나오는거 통합 api 적용중이라 추후 삭제될 API
    public ResponseEntity getFriendCalendar(Long friendId , Principaldetail principaldetail) {
        boolean isMe = false;
        if(Objects.equals(friendId, principaldetail.getMember().getId())){
            isMe = true;
        }
        HashMap<String, List<String>> twolist = new LinkedHashMap<>();
        List<MonthCalendarDto> monthGoalsDtoList = new ArrayList<>();
        Stack<String> dayCheck = new Stack<>();
        Stack<Long> id = new Stack<>();
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endDate = today.with(TemporalAdjusters.lastDayOfMonth());
        List<CalendarGoalsDto> calendarGoalsDtoList;

        if(isMe){
            calendarGoalsDtoList = goalRepository.getMyCalendar(startDate, endDate,principaldetail.getMember().getSocialId());
        }
        else {
            if (friendId != null) {
                calendarGoalsDtoList = goalRepository.getFriendCalendar(startDate, endDate, false, friendId);
            } else throw new IllegalArgumentException("친구 아이디를 입력 하셔야 합니다.!");
        }

        calendarGoalsDtoList.forEach(calendarGoalsDto->CollectSameDate(calendarGoalsDto,twolist,dayCheck,monthGoalsDtoList,id));
        List<TodayGoalsDto> todayGoalsDtoList = calendarGoalsDtoList.stream().filter(c->c.getCurrentDate().equals(today.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")).substring(0,13)))
                .map(CalendarGoalsDto::getTodayGoalsDto).filter(g->checkMe(g,principaldetail.getMember().getId())).collect(Collectors.toList());

        if(!dayCheck.isEmpty()) {
            monthGoalsDtoList.add(MonthCalendarDto.builder().id(id.pop()).currentDate(dayCheck.peek()).charactorColorlist(twolist.get(dayCheck.pop())).build());
        }

        if(isMe == false) {
            CalendarFriendUniteDto calenderFriendUniteDto = CalendarFriendUniteDto.builder().startDate(startDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")))
                    .endDate(endDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")))
                    .monthCalenderDtoList(monthGoalsDtoList)
                    .todayGoalsDtoList(todayGoalsDtoList)
                    .isMe(isMe)
                    .msgDto(GenerateMsg.getMsg(HttpServletResponse.SC_OK, "친구 캘린더 조회에 성공하셨습니다.!"))
                    .build();
            return ResponseEntity.ok().body(calenderFriendUniteDto);
        }

        else {
            CalendarFriendUniteDto calenderFriendUniteDto = CalendarFriendUniteDto.builder().startDate(startDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")))
                    .endDate(endDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")))
                    .monthCalenderDtoList(monthGoalsDtoList)
                    .todayGoalsDtoList(todayGoalsDtoList)
                    .isMe(isMe)
                    .msgDto(GenerateMsg.getMsg(HttpServletResponse.SC_OK, principaldetail.getMember().getNickname() + "의 캘린더 조회에 성공하셨습니다.!"))
                    .build();
            return ResponseEntity.ok().body(calenderFriendUniteDto);
        }
    }

    //TODO :  캘린더에서 특정 날짜 달 눌러서 데이터 나오는거 통합 api 적용중
    public ResponseEntity findMemberSpecificMonth(CalenderRequestDto calenderRequestDto, Principaldetail principaldetail) {
        boolean isMe = false;
        if(Objects.equals(calenderRequestDto.getMemberId(), principaldetail.getMember().getId())){
            isMe = true;
        }

        if(calenderRequestDto.getCalenderMonth() == null){
            throw new IllegalArgumentException("달을 입력하셔야 합니다.");
        }

        List<MonthCalendarDto> monthGoalsDtoList = new ArrayList<>();
        Stack<String> dayCheck = new Stack<>();
        Stack<Long> id = new Stack<>();
        LocalDate localDate = LocalDate.now().withMonth(calenderRequestDto.getCalenderMonth());
        LocalDate startDate = localDate.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endDate = localDate.with(TemporalAdjusters.lastDayOfMonth());
        HashMap<String, List<String>> twolist = new LinkedHashMap<>();
        List<CalendarGoalsDto> calendarGoalsDtoList;
        if(isMe){
            calendarGoalsDtoList = goalRepository.getSpecificCalender(startDate, endDate,localDate,principaldetail.getMember().getSocialId());
        }else {
            if (calenderRequestDto.getMemberId() != null) {
                calendarGoalsDtoList = goalRepository.getFriendSpecificCalendar(startDate, endDate,localDate,false, calenderRequestDto.getMemberId());
            } else throw new IllegalArgumentException("친구 아이디를 입력 하셔야 합니다.!");
        }
        calendarGoalsDtoList.forEach(calendarGoalsDto->CollectSameDate(calendarGoalsDto,twolist,dayCheck,monthGoalsDtoList,id));
        List<TodayGoalsDto> todayGoalsDtoList = calendarGoalsDtoList.stream().filter(c->c.getCurrentDate().equals(localDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")).substring(0,13)))
                .map(CalendarGoalsDto::getTodayGoalsDto).filter(g->checkMe(g,principaldetail.getMember().getId())).collect(Collectors.toList());
        if(!dayCheck.isEmpty()) {
            monthGoalsDtoList.add(MonthCalendarDto.builder().id(id.pop()).currentDate(dayCheck.peek()).charactorColorlist(twolist.get(dayCheck.pop())).build());
        }
        if(isMe == false) {
            CalendarFriendUniteDto calenderFriendUniteDto = CalendarFriendUniteDto.builder().startDate(startDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")))
                    .endDate(endDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")))
                    .monthCalenderDtoList(monthGoalsDtoList)
                    .todayGoalsDtoList(todayGoalsDtoList)
                    .isMe(isMe)
                    .msgDto(GenerateMsg.getMsg(HttpServletResponse.SC_OK, "친구 캘린더 조회에 성공하셨습니다.!"))
                    .build();
            return ResponseEntity.ok().body(calenderFriendUniteDto);
        }
        else {
            CalendarFriendUniteDto calenderFriendUniteDto = CalendarFriendUniteDto.builder().startDate(startDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")))
                    .endDate(endDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")))
                    .monthCalenderDtoList(monthGoalsDtoList)
                    .todayGoalsDtoList(todayGoalsDtoList)
                    .isMe(isMe)
                    .msgDto(GenerateMsg.getMsg(HttpServletResponse.SC_OK, principaldetail.getMember().getNickname() + "의 캘린더 조회에 성공하셨습니다.!"))
                    .build();
            return ResponseEntity.ok().body(calenderFriendUniteDto);
        }
    }

    private boolean checkMe(TodayGoalsDto todayGoalsDto, Long memberId) {
        todayGoalsDto.setMe(Objects.equals(todayGoalsDto.getId(), memberId));
        return true;
    }
}
