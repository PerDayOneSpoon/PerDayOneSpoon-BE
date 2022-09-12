package com.sparta.perdayonespoon.repository;

//import com.sparta.perdayonespoon.domain.dto.CountDto;
import com.sparta.perdayonespoon.domain.dto.CountDto;
import com.sparta.perdayonespoon.domain.dto.response.Goal.TodayGoalsDto;
import com.sparta.perdayonespoon.domain.dto.response.rate.GoalRateDto;
import com.sparta.perdayonespoon.domain.dto.response.Goal.GoalResponseDto;

        import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface GoalRepositoryCustom  {

    List<GoalRateDto> getRateGoal(LocalDateTime sunday, LocalDateTime saturday, String socialId);

    Optional<CountDto> getCountGoal(LocalDateTime currentdate);

    List<TodayGoalsDto> getTodayGoal(LocalDateTime currentdate);
}
