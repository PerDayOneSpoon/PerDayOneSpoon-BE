package com.sparta.perdayonespoon.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    @GetMapping("/health")
    public String healthcheck(){
        return "Pleas12334";
    }
}
