package com.sparta.perdayonespoon.util;

import com.sparta.perdayonespoon.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import javax.mail.MessagingException;
import java.io.IOException;


@Service
@RequiredArgsConstructor
public class MailUtil {
    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String fromMail;

    // 최초 회원가입시 메일 전송
    @Async
    public void RegisterMail(Member member) throws MessagingException, IOException {
        MailHandler mailHandler = new MailHandler(mailSender);
        String nickname = member.getNickname();

        // 받는 사람
        mailHandler.setTo(member.getEmail());
        // 보내는 사람
        mailHandler.setFrom(fromMail);
        // 제목
        mailHandler.setSubject("[하루 한 줌] 회원님의 회원가입을 축하해요! 😍");
        // 내용 (HTML Layout)
        String htmlContent ="<p>" +
                "<div><img src='cid:spoon-main' style='height:350px' align='left' hspace='15'></div>" +
                "<span style='font-size: large'; color: black'>" +
                "<img src='cid:spoon-add' style='width:200px'> <br><br><br>" +
                "안녕하세요, 오늘의 습관 <span style='color: #1f70de; font-weight: bold'>하루 한 줌\uD83C\uDF81️</span> 입니다! <br><br>" +
                "하루 한 줌의 아기자기한 캐릭터들이 기다리고 있어요<br>" +
                "<span style='font-weight: bold'>특별했던 "+nickname+"님의 습관을 <span style='color: #1f70de; font-weight: bold'>하루 한 줌</span>에 만들어 주세요!</span><br>" +
                "긍정적인 생활 습관을 하루 한 줌과 함께 만들어가요!!🥰 <br><br>" +
                "<span style='font-weight: bold'>(속닥속닥) <span style='color: #1f70de; font-weight: bold'>하루 한 줌</span>에 습관을 만들어 주시면, " +
                "<span style='color: #FD574A; font-weight: bold'>웰컴 뱃지와 함께!</span> 환영 해드려요..!🤫</span> <br><br>" +
                "<a href='https://www.perday-onespoon.com' style='font-weight: bold'>💙️"+nickname+"님의 캐릭터와 함께 습관 만들러가기!💙</a>" +
                "<br> <br>" +
                "</span>" +
                "<p>";
        mailHandler.setText(htmlContent, true);
        // 이미지 삽입
        mailHandler.setInline("spoon-add", "static/Per-Day-One-Spoon-Charactor.PNG");
        mailHandler.setInline("spoon-main", "static/Per-dayOne-spoon-Register.jpg");

        mailHandler.send();
    }
}
