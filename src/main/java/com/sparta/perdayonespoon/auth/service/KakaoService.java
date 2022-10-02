package com.sparta.perdayonespoon.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.perdayonespoon.domain.*;
import com.sparta.perdayonespoon.auth.dto.KakaoProfile;
import com.sparta.perdayonespoon.auth.dto.OauthToken;
import com.sparta.perdayonespoon.domain.dto.response.MemberResponseDto;
import com.sparta.perdayonespoon.domain.dto.response.MsgDto;
import com.sparta.perdayonespoon.domain.dto.response.TokenDto;
import com.sparta.perdayonespoon.jwt.Principaldetail;
import com.sparta.perdayonespoon.jwt.TokenProvider;
import com.sparta.perdayonespoon.mapper.MemberMapper;
import com.sparta.perdayonespoon.repository.ImageRepository;
import com.sparta.perdayonespoon.repository.MemberRepository;
import com.sparta.perdayonespoon.repository.RefreshTokenRepository;
import com.sparta.perdayonespoon.sse.domain.repository.EmitterRepository;
import com.sparta.perdayonespoon.util.HeaderUtil;
import com.sparta.perdayonespoon.util.MailUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class KakaoService {
    private final EmitterRepository emitterRepository;
    private final HeaderUtil headerUtil;

    private final MailUtil mailUtil;

    private final RefreshTokenRepository refreshTokenRepository;

    private final TokenProvider tokenProvider;

    private final PasswordEncoder passwordEncoder;

    private final RestTemplate restTemplate;

    private final MemberRepository memberRepository;

    private final ImageRepository imageRepository;

    @Value("${spring.security.oauth2.client.provider.kakao.tokenUri}")
    private String KAKAO_SNS_LOGIN_URL;

    @Value("${spring.security.oauth2.client.registration.kakao.clientId}")
    private String KAKAO_SNS_CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.kakao.redirectUri}")
    private String KAKAO_SNS_CALLBACK_URL;

    @Value("${spring.security.oauth2.client.registration.kakao.clientSecret}")
    private String KAKAO_SNS_CLIENT_SECRET;

    @Value("${spring.security.oauth2.client.provider.kakao.userInfoUri}")
    private String KAKAO_SNS_User_URL;

    @Transactional
    public ResponseEntity<MemberResponseDto> login(String code) throws MessagingException, IOException {

        // 인가코드로 토큰받기
        OauthToken oauthToken = getAccessToken(code);

        //토큰으로 유저정보
        Member member = saveUser(oauthToken.getAccess_token());

        // 토큰발급
        TokenDto tokenDto = generateToken(member);

        // 리턴할 헤더 제작
        HttpHeaders httpHeaders = headerUtil.getHttpHeaders(tokenDto);

        // 리턴할 바디 제작
        MemberResponseDto memberResponseDto = MemberMapper.INSTANCE.orderToDto(member);

        //리턴 바디 상태 코드 및 메세지 넣기
        memberResponseDto.setTwoField(MsgDto.builder().code(HttpServletResponse.SC_OK).msg("로그인이 성공하셨습니다.").build());

        return ResponseEntity.ok().headers(httpHeaders).body(memberResponseDto);
    }

    public OauthToken getAccessToken(String code) {
        //(3)
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        //(4)
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", KAKAO_SNS_CLIENT_ID);
        params.add("redirect_uri", KAKAO_SNS_CALLBACK_URL);
        params.add("code", code);
        params.add("client_secret", KAKAO_SNS_CLIENT_SECRET); // 생략 가능!

        //(5)
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(params, headers);

        // 밑에 바꾼거 때매 그런가?
        ResponseEntity<String> tokenResponse1 = restTemplate.postForEntity(KAKAO_SNS_LOGIN_URL,kakaoTokenRequest,String.class);
        //(6)

        //(7)
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OauthToken oauthToken = null;
        try {
            oauthToken = objectMapper.readValue(tokenResponse1.getBody(), OauthToken.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return oauthToken; //(8)
    }


    private Member saveUser(String access_token) throws MessagingException, IOException {
        KakaoProfile profile = findProfile(access_token);
        //(2)
        Optional<Member> checkmember = memberRepository.findBySocialId(profile.getId());
        //(3)
        if(checkmember.isEmpty()) {
            Member member = Member.builder()
                    .socialId(profile.getId())
                    .nickname(profile.getKakao_account().getProfile().getNickname())
                    .socialCode(profile.getId().substring(0,5)+UUID.randomUUID().toString().charAt(0))
                    .email(profile.getKakao_account().getEmail())
                    .authority(Authority.ROLE_USER)
                    .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .build();
            memberRepository.save(member);
            Image image = Image.builder()
                    .ImgUrl(profile.getKakao_account().getProfile().getProfile_image_url())
                    .build();
            image.setMember(member);
            imageRepository.save(image);
            mailUtil.RegisterMail(member);
            emitterRepository.save(member.getSocialId()+1,new SseEmitter(45000L));
//            String message = member.getNickname()+ "님 회원가입을 환영합니다. 발송된 이메일도 확인해보세요!! 📧";
//            notificationService.send(BadgeSseDto.builder()
//                    .notificationType(NotificationType.Notice)
//                    .message(message)
//                    .member(member)
//                    .build());
            return member;
        }
        else
            return checkmember.get();
    }

    private KakaoProfile findProfile(String token) {

        //(1-3)
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token); //(1-4)
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        //(1-5)
        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest =
                new HttpEntity<>(headers);

        //(1-6)
        // Http 요청 (POST 방식) 후, response 변수에 응답을 받음
        ResponseEntity<String> kakaoProfileResponse = restTemplate.postForEntity(KAKAO_SNS_User_URL,kakaoProfileRequest,String.class);

        //(1-7)
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        KakaoProfile kakaoProfile = null;
        try {
            kakaoProfile = objectMapper.readValue(kakaoProfileResponse.getBody(), KakaoProfile.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return kakaoProfile;
    }

    public TokenDto generateToken(Member member) {
        Principaldetail principaldetail = new Principaldetail(member);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principaldetail, null, principaldetail.getAuthorities());
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        RefreshToken refreshToken = RefreshToken.builder()
                .key(member.getSocialId())
                .value(tokenDto.getRefreshToken())
                .build();

        refreshTokenRepository.save(refreshToken);
        return tokenDto;
    }
}
