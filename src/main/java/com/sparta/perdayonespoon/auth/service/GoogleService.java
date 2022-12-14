package com.sparta.perdayonespoon.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.perdayonespoon.domain.*;
import com.sparta.perdayonespoon.auth.dto.GoogleProfile;
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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class GoogleService {

    private final ApplicationEventPublisher eventPublisher;
    private final EmitterRepository emitterRepository;
    private final HeaderUtil headerUtil;

    private final RefreshTokenRepository refreshTokenRepository;

    private final TokenProvider tokenProvider;

    private final PasswordEncoder passwordEncoder;

    private final MemberRepository memberRepository;

    private final RestTemplate restTemplate;

    private final ImageRepository imageRepository;

    @Value("${spring.security.oauth2.client.provider.google.tokenUri}")
    private String GOOGLE_SNS_LOGIN_URL;

    @Value("${spring.security.oauth2.client.registration.google.clientId}")
    private String GOOGLE_SNS_CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.google.redirectUri}")
    private String GOOGLE_SNS_CALLBACK_URL;

    @Value("${spring.security.oauth2.client.registration.google.clientSecret}")
    private String GOOGLE_SNS_CLIENT_SECRET;

    @Value("${spring.security.oauth2.client.provider.google.userInfoUri}")
    private String GOOGLE_SNS_User_URL;


    @Transactional
    public ResponseEntity<MemberResponseDto> login(String code) throws MessagingException, IOException {
        // ??????????????? ????????????
        OauthToken oauthToken = getAccessToken(code);
        // ???????????? ????????? ?????? ??????
        Member member = saveUser(oauthToken.getAccess_token());
        // ????????? ????????? ????????? ????????????
        TokenDto tokenDto = generateToken(member);
        // ????????? ?????? ??????
        HttpHeaders httpHeaders = headerUtil.getHttpHeaders(tokenDto);
        // ????????? ?????? ??????
        MemberResponseDto memberResponseDto = MemberMapper.INSTANCE.orderToDto(member);
        //?????? ?????? ?????? ?????? ??? ????????? ??????
        memberResponseDto.setTwoField(MsgDto.builder().code(HttpStatus.OK.value()).msg("???????????? ?????????????????????.").build());
        return ResponseEntity.ok().headers(httpHeaders).body(memberResponseDto);
    }
    private OauthToken getAccessToken(String code) {
        String decodedCode = "";
        decodedCode = java.net.URLDecoder.decode(code, StandardCharsets.UTF_8);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        //(4)
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", GOOGLE_SNS_CLIENT_ID);
        params.add("client_secret", GOOGLE_SNS_CLIENT_SECRET);
        params.add("code", decodedCode);
        params.add("redirect_uri", GOOGLE_SNS_CALLBACK_URL);
        params.add("grant_type", "authorization_code");
        //(5)
        HttpEntity<MultiValueMap<String, String>> googleTokenRequest = new HttpEntity<>(params, headers);

        ResponseEntity<String> tokenResponse1 = restTemplate.postForEntity(GOOGLE_SNS_LOGIN_URL,googleTokenRequest,String.class);
        //(6)
        ObjectMapper objectMapper = new ObjectMapper();
        //(7)
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OauthToken oauthToken = null;
        try {
            oauthToken = objectMapper.readValue(tokenResponse1.getBody(), OauthToken.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return oauthToken; //(8)
    }

    @Transactional
    public Member saveUser(String access_token) throws MessagingException, IOException {
        GoogleProfile profile = findProfile(access_token);
        //(2)
        Optional<Member> checkmember = memberRepository.findBySocialId(profile.getSub());
        //(3)
        if(checkmember.isEmpty()) {
            Member member = Member.builder()
                    .socialId(profile.getSub())
                    .socialCode(profile.getSub().substring(0,5)+UUID.randomUUID().toString().charAt(0))
                    .nickname(profile.getName())
                    .email(profile.getEmail())
                    .authority(Authority.ROLE_USER)
                    .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .build();
            memberRepository.save(member);
            Image image = Image.builder()
                    .ImgUrl(profile.getPicture())
                    .build();
            image.setMember(member);
            imageRepository.save(image);
            eventPublisher.publishEvent(member);
//            mailUtil.RegisterMail(member);
            emitterRepository.save(member.getSocialId()+1,new SseEmitter(45000L));
            return member;
        }
        return checkmember.get();
    }

    private GoogleProfile findProfile(String token) {
        //(1-3)
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token); //(1-4)
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        //(1-5)
        HttpEntity<MultiValueMap<String, String>> googleProfileRequest =
                new HttpEntity<>(headers);
        ResponseEntity<String> googleProfileResponse = restTemplate.postForEntity(GOOGLE_SNS_User_URL,googleProfileRequest,String.class);
        //(1-7)
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        GoogleProfile googleProfile = null;
        try {
            googleProfile = objectMapper.readValue(googleProfileResponse.getBody(), GoogleProfile.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return googleProfile;
    }

    private TokenDto generateToken(Member member) {
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
