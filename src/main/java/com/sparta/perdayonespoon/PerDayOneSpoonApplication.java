package com.sparta.perdayonespoon;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
@EnableEncryptableProperties
public class PerDayOneSpoonApplication {

    public static void main(String[] args) {
        SpringApplication.run(PerDayOneSpoonApplication.class, args);
    }

}
