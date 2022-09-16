package com.sparta.perdayonespoon.emailtest;


import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/email")
    public void sendEmail() {
        EmailMessage emailMessage = EmailMessage.builder()
                .to("royycc65@gmail.conm")  // 받는 메일 주소
                .subject("하루 한줌을 시작하신것을 진심으로 환영합니다!") // 메일 제목
                .message("이제부터 여러분의 삶을 위한 건강한 루틴 만들기에 성공하시도록 도와드리겠습니다!") // 메일 본문, HTML 형식도 가능
                .build();                                           // Thymeleaf 를 이용해 정적 HTML 파일도 응용 가능

        emailService.sendEmail(emailMessage);
    }
}
