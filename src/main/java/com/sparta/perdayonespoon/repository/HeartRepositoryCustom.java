package com.sparta.perdayonespoon.repository;

import com.sparta.perdayonespoon.domain.Goal;

import java.util.List;

public interface HeartRepositoryCustom {


    List<Goal> findGoalsHeart(String goalFlag);

}
