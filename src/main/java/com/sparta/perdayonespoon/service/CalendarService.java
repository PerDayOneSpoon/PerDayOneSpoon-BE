package com.sparta.perdayonespoon.service;
import com.sparta.perdayonespoon.domain.Member;
import com.sparta.perdayonespoon.domain.dto.request.CalendarRequestDto;
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
import com.sparta.perdayonespoon.util.MsgUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@RequiredArgsConstructor
@Service
public class CalendarService {

    private final MsgUtil msgUtil;
    private final MemberRepository memberRepository;
    private final FriendRepository friendRepository;
    private final GoalRepository goalRepository;

    //TODO : 여기는 캘린더를 들어왔을때 모든걸 보여주는 함수
    public ResponseEntity getAlldate(Principaldetail principaldetail) {
        List<FriendDto> peopleList = friendRepository.getFollowerList(principaldetail.getMember().getSocialId());
        Member myMember = memberRepository.findBySocialId(principaldetail.getMember().getSocialId()).orElseThrow(
                () ->new IllegalArgumentException("해당 유저가 없습니다"));
        peopleList.add(0,FriendDto.builder().isMe(true).id(myMember.getId()).socialId(myMember.getSocialId()).nickname(myMember.getNickname()).status(myMember.getStatus()).profileImage(myMember.getImage().getImgUrl()).build());
        CalendarUniteDto calenderUniteDto = CalendarUniteDto.builder()
                .peopleList(peopleList)
                .msgDto(msgUtil.getMsg(HttpServletResponse.SC_OK,"금일 캘린더 조회에 성공하셨습니다.!"))
                .build();
        return ResponseEntity.ok().body(calenderUniteDto);
    }

    private void CollectSameDate(CalendarGoalsDto calenderGoalsDtoList,Map<String,List<String>> twolist, Queue<String> daycheck, List<MonthCalendarDto> monthCalenderDtoList,Queue<Long> id) {
        if(twolist.containsKey(calenderGoalsDtoList.getCurrentDate())) {
            twolist.get(calenderGoalsDtoList.getCurrentDate()).add(calenderGoalsDtoList.getCharactorColor());
        }
        else {
            if(daycheck.isEmpty()) {
                id.offer(calenderGoalsDtoList.getId());
                daycheck.offer(calenderGoalsDtoList.getCurrentDate());
                List<String> charactorColors = new ArrayList<>();
                charactorColors.add(calenderGoalsDtoList.getCharactorColor());
                twolist.put(calenderGoalsDtoList.getCurrentDate(), charactorColors);
            }
            else
            {
                monthCalenderDtoList.add(MonthCalendarDto.builder().id(id.poll()).currentDate(daycheck.element()).charactorColorlist(twolist.get(daycheck.poll())).build());
                daycheck.offer(calenderGoalsDtoList.getCurrentDate());
                id.offer(calenderGoalsDtoList.getId());
                List<String> charactorColors = new ArrayList<>();
                charactorColors.add(calenderGoalsDtoList.getCharactorColor());
                twolist.put(calenderGoalsDtoList.getCurrentDate(), charactorColors);
            }
        }
    }
    //TODO :  캘린더에서 특정 날짜 눌러서 데이터 나오는거 통합 api 적용
    public ResponseEntity findMemberSpecificDate(CalendarRequestDto calendarRequestDto, Principaldetail principaldetail) {
        assert calendarRequestDto.getCalendarDate() != null;
        boolean isMe = false;
        LocalDate localDate = LocalDate.parse(calendarRequestDto.getCalendarDate());
        LocalDateTime localDateTime = LocalDateTime.of(localDate.getYear(), localDate.getMonth(), localDate.getDayOfMonth(), 0, 0, 0);
        List<TodayGoalsDto> todayGoalsDtoList;
        if(Objects.equals(calendarRequestDto.getMemberId(), principaldetail.getMember().getId())) {
            todayGoalsDtoList = goalRepository.getTodayGoal(localDateTime, principaldetail.getMember().getSocialId());
        } else {
            todayGoalsDtoList = goalRepository.getFriendTodayGoal(localDateTime, calendarRequestDto.getMemberId(),principaldetail.getMember().getSocialId(), false);
        }
        if(Objects.equals(calendarRequestDto.getMemberId(), principaldetail.getMember().getId())) isMe = true;
        DayGoalsDto dayGoalsDto = DayGoalsDto.builder().todayGoalsDtoList(todayGoalsDtoList).isMe(isMe).build();
        return ResponseEntity.ok().body(dayGoalsDto);
    }

    //TODO :  캘린더에서 특정 날짜 눌러서 데이터 나오는거 통합 api 적용중이라 추후 삭제될 API
    public ResponseEntity getFriendCalendar(Long friendId , Principaldetail principaldetail) {
        HashMap<String, List<String>> twolist = new LinkedHashMap<>();
        List<MonthCalendarDto> monthGoalsDtoList = new ArrayList<>();
        Queue<String> dayCheck = new LinkedList<>();
        Queue<Long> id = new LinkedList<>();
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endDate = today.with(TemporalAdjusters.lastDayOfMonth());
        List<CalendarGoalsDto> calendarGoalsDtoList;
        if(Objects.equals(friendId, principaldetail.getMember().getId())){
            calendarGoalsDtoList = goalRepository.getMyCalendar(startDate, endDate,principaldetail.getMember().getSocialId());
        }
        else {
            if (friendId != null) {
                calendarGoalsDtoList = goalRepository.getFriendCalendar(startDate, endDate, false, friendId);
            } else throw new IllegalArgumentException("친구 아이디를 입력 하셔야 합니다.!");
        }

        calendarGoalsDtoList.forEach(calendarGoalsDto->CollectSameDate(calendarGoalsDto,twolist,dayCheck,monthGoalsDtoList,id));
        if(!dayCheck.isEmpty()) {
            monthGoalsDtoList.add(MonthCalendarDto.builder().id(id.poll()).currentDate(dayCheck.element()).charactorColorlist(twolist.get(dayCheck.poll())).build());
        }
        if(!Objects.equals(friendId, principaldetail.getMember().getId())) {
            CalendarFriendUniteDto calenderFriendUniteDto = CalendarFriendUniteDto.builder().startDate(startDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")))
                    .endDate(endDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")))
                    .monthCalenderDtoList(monthGoalsDtoList)
                    .msgDto(msgUtil.getMsg(HttpServletResponse.SC_OK, "친구 캘린더 조회에 성공하셨습니다.!"))
                    .build();
            return ResponseEntity.ok().body(calenderFriendUniteDto);
        }

        else {
            CalendarFriendUniteDto calenderFriendUniteDto = CalendarFriendUniteDto.builder().startDate(startDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")))
                    .endDate(endDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")))
                    .monthCalenderDtoList(monthGoalsDtoList)
                    .msgDto(msgUtil.getMsg(HttpServletResponse.SC_OK, principaldetail.getMember().getNickname() + "의 캘린더 조회에 성공하셨습니다.!"))
                    .build();
            return ResponseEntity.ok().body(calenderFriendUniteDto);
        }
    }

    //TODO :  캘린더에서 특정 날짜 달 눌러서 데이터 나오는거 통합 api 적용중
    public ResponseEntity findMemberSpecificMonth(CalendarRequestDto calendarRequestDto, Principaldetail principaldetail) {
        if(calendarRequestDto.getCalendarYearAndMonth() == null){
            throw new IllegalArgumentException("달을 입력하셔야 합니다.");
        }
        String [] yearOrMonth = calendarRequestDto.getCalendarYearAndMonth().split("-");
        List<MonthCalendarDto> monthGoalsDtoList = new ArrayList<>();
        Queue<String> dayCheck = new LinkedList<>();
        Queue<Long> id = new LinkedList<>();
        LocalDate localDate = LocalDate.now().withYear(Integer.parseInt(yearOrMonth[0])).withMonth(Integer.parseInt(yearOrMonth[1]));
        LocalDate startDate = localDate.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endDate = localDate.with(TemporalAdjusters.lastDayOfMonth());
        HashMap<String, List<String>> twolist = new LinkedHashMap<>();
        List<CalendarGoalsDto> calendarGoalsDtoList;
        if(Objects.equals(calendarRequestDto.getMemberId(), principaldetail.getMember().getId())){
            calendarGoalsDtoList = goalRepository.getSpecificCalender(startDate, endDate,localDate,principaldetail.getMember().getSocialId());
        }else {
            if (calendarRequestDto.getMemberId() != null) {
                calendarGoalsDtoList = goalRepository.getFriendSpecificCalendar(startDate, endDate,localDate,false, calendarRequestDto.getMemberId());
            } else throw new IllegalArgumentException("친구 아이디를 입력 하셔야 합니다.!");
        }
        calendarGoalsDtoList.forEach(calendarGoalsDto->CollectSameDate(calendarGoalsDto,twolist,dayCheck,monthGoalsDtoList,id));
        if(!dayCheck.isEmpty()) {
            monthGoalsDtoList.add(MonthCalendarDto.builder().id(id.poll()).currentDate(dayCheck.element()).charactorColorlist(twolist.get(dayCheck.poll())).build());
        }
        if(!Objects.equals(calendarRequestDto.getMemberId(), principaldetail.getMember().getId())) {
            CalendarFriendUniteDto calenderFriendUniteDto = CalendarFriendUniteDto.builder().startDate(startDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")))
                    .endDate(endDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")))
                    .monthCalenderDtoList(monthGoalsDtoList)
                    .msgDto(msgUtil.getMsg(HttpServletResponse.SC_OK, "친구 캘린더 조회에 성공하셨습니다.!"))
                    .build();
            return ResponseEntity.ok().body(calenderFriendUniteDto);
        }
        else {
            CalendarFriendUniteDto calenderFriendUniteDto = CalendarFriendUniteDto.builder().startDate(startDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")))
                    .endDate(endDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")))
                    .monthCalenderDtoList(monthGoalsDtoList)
                    .msgDto(msgUtil.getMsg(HttpServletResponse.SC_OK, principaldetail.getMember().getNickname() + "의 캘린더 조회에 성공하셨습니다.!"))
                    .build();
            return ResponseEntity.ok().body(calenderFriendUniteDto);
        }
    }
}
