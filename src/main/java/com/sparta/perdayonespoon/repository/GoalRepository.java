package com.sparta.perdayonespoon.repository;

import com.sparta.perdayonespoon.domain.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findAllBySocialId(String socialId);
}
