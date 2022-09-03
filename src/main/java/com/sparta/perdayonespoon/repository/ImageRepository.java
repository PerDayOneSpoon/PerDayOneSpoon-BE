package com.sparta.perdayonespoon.repository;

import com.sparta.perdayonespoon.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;



public interface ImageRepository extends JpaRepository<Image, Long> {
}
