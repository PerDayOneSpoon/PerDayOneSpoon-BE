package com.sparta.perdayonespoon.domain.dto.response.Goal;

import com.querydsl.core.annotations.QueryProjection;
import com.sparta.perdayonespoon.domain.Goal;
import com.sparta.perdayonespoon.domain.Heart;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import java.util.List;

@Getter
@NoArgsConstructor
public class GoalsAndHeart {

    private List<Goal> goalList;


    private List<Heart> heartList;

    @Builder
    public GoalsAndHeart(List<Goal> goalList, List<Heart> heartList){
        this.goalList = goalList;
        this.heartList = heartList;
    }
}
