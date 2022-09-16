//package com.sparta.perdayonespoon.emailtest;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.mail.javamail.JavaMailSenderImpl;
//
//@Slf4j
//@Configuration
//public class EmailConfig {
//
//    @Value("${spring.mail.host}")
//    String emailHost;
//
//    @Value("${spring.mail.properties}")
//    String emailProtocol;
//
//    @Value("${spring.mail.port}")
//    int emailPort;
//
//    @Bean
//    public JavaMailSenderImpl mailSender() {
//        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
//        javaMailSender.setProtocol(emailProtocol);
//        javaMailSender.setHost(emailHost);
//        javaMailSender.setPort(emailPort);
//        return javaMailSender;
//    }
//}
