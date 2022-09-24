package com.sparta.perdayonespoon.repository;

import com.sparta.perdayonespoon.domain.Heart;
import com.sparta.perdayonespoon.domain.dto.response.Goal.GoalsAndHeart;

import java.util.List;

public interface HeartRepositoryCustom {


    List<GoalsAndHeart> findGoalsHeart(String goalFlag);
}
