package com.sparta.perdayonespoon.service;

import com.sparta.perdayonespoon.jwt.Principaldetail;
import com.sparta.perdayonespoon.repository.GoalRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

@Service
public class CalenderService {

    private GoalRepository goalRepository;

    public ResponseEntity getAlldate(Principaldetail principaldetail) {
        Long id = principaldetail.getMember().getId();
        return ResponseEntity.ok().body(id);
    }
}
