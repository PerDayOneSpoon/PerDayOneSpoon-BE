package com.sparta.perdayonespoon.emailtest;


import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.internet.AddressException;

@RestController
@AllArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/email")
    public void sendEmail() throws Exception {
        emailService.sendEmail();
    }
}
