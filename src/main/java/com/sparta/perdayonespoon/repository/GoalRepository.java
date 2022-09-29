package com.sparta.perdayonespoon.repository;

import com.sparta.perdayonespoon.domain.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GoalRepository extends JpaRepository<Goal, Long>, GoalRepositoryCustom {
    List<Goal> findAllBySocialId(String socialId);

}
