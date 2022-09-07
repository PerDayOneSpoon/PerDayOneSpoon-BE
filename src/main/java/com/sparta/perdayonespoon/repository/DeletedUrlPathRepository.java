package com.sparta.perdayonespoon.repository;

import com.sparta.perdayonespoon.domain.DeletedUrlPath;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeletedUrlPathRepository extends JpaRepository<DeletedUrlPath, Long> {
}