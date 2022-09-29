package com.sparta.perdayonespoon.repository;

import com.sparta.perdayonespoon.domain.Badge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BadgeRepository extends JpaRepository<Badge, Long>,BadgeRepositoryCustom {
}
