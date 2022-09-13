package com.sparta.perdayonespoon.service;

import com.sparta.perdayonespoon.domain.dto.response.Goal.MonthGoalsDto;
import com.sparta.perdayonespoon.jwt.Principaldetail;
import com.sparta.perdayonespoon.repository.GoalRepository;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.jni.Local;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CalenderService {

    private final GoalRepository goalRepository;

    public ResponseEntity getAlldate(Principaldetail principaldetail) {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endDate = today.with(TemporalAdjusters.lastDayOfMonth());
        List<MonthGoalsDto> monthGoalsDtos = goalRepository.getMonthGoal(startDate, endDate,
                                                                        principaldetail.getMember().getSocialId());
        return ResponseEntity.ok().body(monthGoalsDtos);
    }
}
