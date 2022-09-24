package com.sparta.perdayonespoon.repository;

import com.sparta.perdayonespoon.domain.Heart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HeartRepository extends JpaRepository<Heart, Long>,HeartRepositoryCustom {
    Optional<Heart> findBySocialId(String socialId);

    boolean existsBySocialIdAndGoal_Id(String socialId, Long id);

    Optional<Heart> findByGoalId(Long id);
}
