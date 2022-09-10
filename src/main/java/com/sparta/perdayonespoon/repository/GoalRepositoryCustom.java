package com.sparta.perdayonespoon.repository;

import com.sparta.perdayonespoon.domain.Goal;
import com.sparta.perdayonespoon.domain.dto.request.MemberSearchCondition;
import com.sparta.perdayonespoon.domain.dto.response.GoalRateDto;
import com.sparta.perdayonespoon.domain.dto.response.MemberSearchDto;

import java.time.LocalDateTime;
import java.util.List;

public interface GoalRepositoryCustom  {

    List<GoalRateDto> getRateGoal(LocalDateTime sunday, LocalDateTime saturday, String socialId);
}
