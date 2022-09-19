package com.sparta.perdayonespoon.emailtest;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Slf4j
@Service
@AllArgsConstructor
public class EmailService{

    private JavaMailSenderImpl javaMailSender;
    private final TemplateEngine templateEngine;

    private static final String FROM_ADDRESS = "HaruHanJum";

//    public void sendEmail(EmailMessage emailMessage) {            // 단순 텍스트 메일
//        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
//        try {
//            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
//            mimeMessageHelper.setFrom(FROM_ADDRESS);    // setFrom 메소드 미호출시 default 는 yml 의 username
//            mimeMessageHelper.setTo(emailMessage.getTo());
//            mimeMessageHelper.setSubject(emailMessage.getSubject());
//            mimeMessageHelper.setText(emailMessage.getMessage(),true);
//            javaMailSender.send(mimeMessage);
//            log.info("Success");
//
//        } catch(MessagingException e) {
//            log.info("fail");
//            throw new RuntimeException();
//
//        }
//    }



    // 내용을 HTML 로 보낼경우
    public void sendEmail() throws Exception {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        InternetAddress from = new InternetAddress("royycc65@gmail.com", "하루한줌");
        Context context = new Context();
        context.setVariable("welcome", "Welcome to 하루한줌!");
        String html = templateEngine.process("WelcomeEmail", context);

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            mimeMessageHelper.setFrom(from);    // setFrom 메소드 미호출시 default 는 yml 의 username
            mimeMessageHelper.setTo("v0o0605@gmail.com");
            mimeMessageHelper.setSubject("하루 한줌 을 시작하신것을 진심으로 환영합니다!");
            mimeMessageHelper.setText(html,true);
            javaMailSender.send(mimeMessage);
            log.info("Success");

        } catch(Exception e) {
            log.info("fail");
            throw new RuntimeException();

        }
    }

//    private String setContext() { // 타임리프 설정하는 코드 따로 뺄 경우
//        Context context = new Context();
//        context.setVariable("welcome", "welcome"); // Template 에 전달할 데이터 설정
//        return templateEngine.process("WelcomeEmail", context); // mail.html
//    }


}
