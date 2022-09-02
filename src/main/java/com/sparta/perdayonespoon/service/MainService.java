package com.sparta.perdayonespoon.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class MainService {
    public ResponseEntity getGoal() {
        return ResponseEntity.ok().build();

    }
}
