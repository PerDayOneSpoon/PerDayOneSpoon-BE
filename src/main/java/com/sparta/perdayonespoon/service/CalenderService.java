package com.sparta.perdayonespoon.service;

import com.sparta.perdayonespoon.domain.SuccessMsg;
import com.sparta.perdayonespoon.domain.dto.response.Goal.TodayGoalsDto;
import com.sparta.perdayonespoon.domain.dto.response.calender.CalenderGoalsDto;
import com.sparta.perdayonespoon.domain.dto.response.calender.CalenderUniteDto;
import com.sparta.perdayonespoon.domain.dto.response.calender.MonthCalenderDto;
import com.sparta.perdayonespoon.jwt.Principaldetail;
import com.sparta.perdayonespoon.repository.GoalRepository;
import com.sparta.perdayonespoon.util.GenerateMsg;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CalenderService {
//    private List<String> charactorColorList = new ArrayList<>(); //  주소값 참조해서 데이터를 가져다 쓰는거라
//    private HashMap<String, List<String>> twolist = new LinkedHashMap<>(); // -> 새로운걸 만들어주는줄알았음
//    private List<MonthCalenderDto> monthGoalsDtoList = new ArrayList<>(); //
//    private final GoalRepository goalRepository;
//
//    public ResponseEntity getAlldate(Principaldetail principaldetail) {
//        LocalDate today = LocalDate.now();
//        LocalDate startDate = today.with(TemporalAdjusters.firstDayOfMonth());
//        LocalDate endDate = today.with(TemporalAdjusters.lastDayOfMonth());
//        List<MonthGoalsDto> monthGoalsDtos = goalRepository.getMonthGoal(startDate, endDate,
//                                                                        principaldetail.getMember().getSocialId());
//        monthGoalsDtos.stream().filter(MonthGoalsDto::isPrivateCheck).forEach(this::CollectSameDate);
//        twolist.forEach(this::addMonthCalenderDto);
//        CalenderUniteDto calenderUniteDto = CalenderUniteDto.builder().startDate(startDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")))
//                .endDate(endDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")))
//                .monthCalenderDtoList(monthGoalsDtoList)
//                .build();
//        if(!charactorColorList.isEmpty()){
//            charactorColorList.add("재준바보");
//        }
//        charactorColorList.clear();
//        twolist.clear();
//        monthGoalsDtoList.clear();
//        return ResponseEntity.ok().body(calenderUniteDto);
//    }
//    private void addMonthCalenderDto(String s, List<String> strings) {
//        monthGoalsDtoList.add(MonthCalenderDto.builder().currentDate(s).charactorColorlist(strings).build());
//    }
//
//    private void CollectSameDate(MonthGoalsDto monthGoalsDto) {
//        if(twolist.containsKey(monthGoalsDto.getCurrentDate())) {
//            twolist.get(monthGoalsDto.getCurrentDate()).add(monthGoalsDto.getCharactorColor());
//        }
//        else {
//            charactorColorList.clear();
//            charactorColorList.add(monthGoalsDto.getCharactorColor());
//            twolist.put(monthGoalsDto.getCurrentDate(), charactorColorList);
//        }
//    }
//}

//    private final GoalRepository goalRepository;
//
//    public ResponseEntity getAlldate(Principaldetail principaldetail) {
//        List<String> charactorColorList = new ArrayList<>();
//        HashMap<String, List<String>> twolist = new LinkedHashMap<>();
//        List<MonthCalenderDto> monthGoalsDtoList = new ArrayList<>();
//        LocalDate today = LocalDate.now();
//        LocalDate startDate = today.with(TemporalAdjusters.firstDayOfMonth());
//        LocalDate endDate = today.with(TemporalAdjusters.lastDayOfMonth());
//        List<MonthGoalsDto> monthGoalsDtos = goalRepository.getMonthGoal(startDate, endDate,false,
//                principaldetail.getMember().getSocialId());
//        String[][] twoArray = new String[30][5];
//        monthGoalsDtos.stream().sorted(Comparator.comparing(MonthGoalsDto::getCurrentDate)).forEach(MonthGoalsDto->CollectSameDate(MonthGoalsDto,charactorColorList,twolist));
//        twolist.forEach((key,value)->monthGoalsDtoList.add(MonthCalenderDto.builder().currentDate(key).charactorColorlist(value).build()));
//        CalenderUniteDto calenderUniteDto = CalenderUniteDto.builder().startDate(startDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")))
//                .endDate(endDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")))
//                .monthCalenderDtoList(monthGoalsDtoList)
//                .build();
//        return ResponseEntity.ok().body(calenderUniteDto);
//    }
//    private void CollectSameDate(MonthGoalsDto monthGoalsDto,List<String> charactorColorList,Map<String,List<String>> twolist) {
//        if(twolist.containsKey(monthGoalsDto.getCurrentDate())) {
//            twolist.get(monthGoalsDto.getCurrentDate()).add(monthGoalsDto.getCharactorColor());
//        }
//        else {
//            charactorColorList.clear();
//            charactorColorList.add(monthGoalsDto.getCharactorColor());
//            twolist.put(monthGoalsDto.getCurrentDate(), charactorColorList);
//        }
//    }

    private final GoalRepository goalRepository;

    public ResponseEntity getAlldate(Principaldetail principaldetail) {
        HashMap<String, List<String>> twolist = new LinkedHashMap<>();
        List<MonthCalenderDto> monthGoalsDtoList = new ArrayList<>();
        Stack<String> dayCheck = new Stack<>();
        Stack<Long> id = new Stack<>();
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endDate = today.with(TemporalAdjusters.lastDayOfMonth());
        List<CalenderGoalsDto> calenderGoalsDtoList = goalRepository.getMonthGoal(startDate, endDate,false,
                principaldetail.getMember().getSocialId());
        calenderGoalsDtoList.forEach(calenderGoalsDto->CollectSameDate(calenderGoalsDto,twolist,dayCheck,monthGoalsDtoList,id));
        List<TodayGoalsDto> todayGoalsDtoList = calenderGoalsDtoList.stream().filter(c->c.getCurrentDate().equals(today.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")).substring(0,13))).map(CalenderGoalsDto::getTodayGoalsDto).collect(Collectors.toList());
        if(!dayCheck.isEmpty()) {
            monthGoalsDtoList.add(MonthCalenderDto.builder().id(id.pop()).currentDate(dayCheck.peek()).charactorColorlist(twolist.get(dayCheck.pop())).build());
        }
        CalenderUniteDto calenderUniteDto = CalenderUniteDto.builder().startDate(startDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")))
                .endDate(endDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")))
                .monthCalenderDtoList(monthGoalsDtoList)
                .todayGoalsDtoList(todayGoalsDtoList)
                .msgDto(GenerateMsg.getMsg(SuccessMsg.GET_CALENDER.getCode(), SuccessMsg.GET_CALENDER.getMsg()))
                .build();
        return ResponseEntity.ok().body(calenderUniteDto);
    }
    private void CollectSameDate(CalenderGoalsDto calenderGoalsDtoList,Map<String,List<String>> twolist, Stack<String> daycheck, List<MonthCalenderDto> monthCalenderDtoList,Stack<Long> id) {
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
                monthCalenderDtoList.add(MonthCalenderDto.builder().id(id.pop()).currentDate(daycheck.peek()).charactorColorlist(twolist.get(daycheck.pop())).build());
                daycheck.push(calenderGoalsDtoList.getCurrentDate());
                id.push(calenderGoalsDtoList.getId());
                List<String> charactorColors = new ArrayList<>();
                charactorColors.add(calenderGoalsDtoList.getCharactorColor());
                twolist.put(calenderGoalsDtoList.getCurrentDate(), charactorColors);
            }
        }
    }
}
