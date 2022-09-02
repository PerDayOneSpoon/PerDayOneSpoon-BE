package com.sparta.perdayonespoon.controller;

import com.sparta.perdayonespoon.service.MainService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags="메인페이지 REST API")
@RequestMapping("api/main/")
@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = "*")
public class MainController {

    private final MainService mainService;

    @GetMapping("/auth")
    public ResponseEntity getGoal() {
        return mainService.getGoal();
    }
}
