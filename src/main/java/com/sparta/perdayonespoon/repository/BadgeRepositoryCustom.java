package com.sparta.perdayonespoon.repository;

import com.sparta.perdayonespoon.domain.Badge;

import java.util.List;

public interface BadgeRepositoryCustom {

    List<Badge> findAllByMember_Id(Long id);
}
