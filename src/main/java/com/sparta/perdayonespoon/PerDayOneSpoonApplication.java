package com.sparta.perdayonespoon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class PerDayOneSpoonApplication {

    public static void main(String[] args) {
        SpringApplication.run(PerDayOneSpoonApplication.class, args);
    }

}
