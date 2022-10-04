package com.sparta.perdayonespoon.repository;


import com.sparta.perdayonespoon.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long>,RefreshTokenRepositoryCustom {
    Optional<RefreshToken> findByKey(String name);

    boolean existsByKey(String socialId);
}

