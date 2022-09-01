package com.sparta.perdayonespoon.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.perdayonespoon.domain.Authority;
import com.sparta.perdayonespoon.domain.Member;
import com.sparta.perdayonespoon.domain.RefreshToken;
import com.sparta.perdayonespoon.domain.auth.GoogleProfile;
import com.sparta.perdayonespoon.domain.dto.OauthToken;
import com.sparta.perdayonespoon.domain.dto.response.MemberResponseDto;
import com.sparta.perdayonespoon.domain.dto.response.TokenDto;
import com.sparta.perdayonespoon.jwt.Principaldetail;
import com.sparta.perdayonespoon.jwt.TokenProvider;
import com.sparta.perdayonespoon.repository.MemberRepository;
import com.sparta.perdayonespoon.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class GoogleService {

    private final RefreshTokenRepository refreshTokenRepository;

    private final TokenProvider tokenProvider;

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final RestTemplate restTemplate;

    @Value("${spring.security.oauth2.client.provider.google.tokenUri}")
    private String GOOGLE_SNS_LOGIN_URL;

    @Value("${spring.security.oauth2.client.registration.google.clientId}")
    private String GOOGLE_SNS_CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String GOOGLE_SNS_CALLBACK_URL;

    @Value("${spring.security.oauth2.client.registration.google.clientSecret}")
    private String GOOGLE_SNS_CLIENT_SECRET;

    @Value("${spring.security.oauth2.client.provider.google.userInfoUri}")
    private String GOOGLE_SNS_User_URL;

    public ResponseEntity login(String code) {

        // 인가코드로 토큰받기
        OauthToken oauthToken = getAccessToken(code);

        Member member = saveUser(oauthToken.getAccess_token());

        // 토큰발급
        TokenDto tokenDto = generateToken(member);

        // 리턴할 헤더 제작
        HttpHeaders headers = generateResponse(tokenDto);

        // 리턴할 바디 제작
        MemberResponseDto memberResponseDto = convertDto(member); // 추후 리팩토링 예정 -> 이거는 맵스트럭트로 리팩토링할예정

        return ResponseEntity.ok().headers(headers).body(memberResponseDto);
    }
    private OauthToken getAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        //(4)
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", GOOGLE_SNS_CLIENT_ID);
        params.add("redirect_uri", GOOGLE_SNS_CALLBACK_URL);
        params.add("code", code);
        params.add("client_secret", GOOGLE_SNS_CLIENT_SECRET); // 생략 가능!

        //(5)
        HttpEntity<MultiValueMap<String, String>> googleTokenRequest =
                new HttpEntity<>(params, headers);

        ResponseEntity<String> tokenResponse1 = restTemplate.postForEntity(GOOGLE_SNS_LOGIN_URL,googleTokenRequest,String.class);
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

    private Member saveUser(String access_token) {
        GoogleProfile profile = findProfile(access_token);
        //(2)
        Optional<Member> checkmember = memberRepository.findBySocialId(profile.getSub());

        //(3)
        if(checkmember.isEmpty()) {
            Member member = Member.builder()
                    .socialId(profile.getSub())
                    .nickname(profile.getName())
                    .email(profile.getEmail())
                    .profileImage(profile.getPicture())
                    .authority(Authority.ROLE_USER)
                    .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .build();

            return memberRepository.save(member);
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

        //(1-6)
        // Http 요청 (POST 방식) 후, response 변수에 응답을 받음
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
                .key(authentication.getName())
                .value(tokenDto.getRefreshToken())
                .build();

        refreshTokenRepository.save(refreshToken);
        return tokenDto;
    }

    private HttpHeaders generateResponse(TokenDto tokenDto){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + tokenDto.getAccessToken());
        headers.add("Refresh-Token", tokenDto.getRefreshToken());
        headers.add("Access-Token-Expire-Time", String.valueOf(tokenDto.getAccessTokenExpiresIn()));
        return headers;
    }

    private MemberResponseDto convertDto(Member member) {
        return MemberResponseDto.builder()
                .id(member.getId())
                .socialCode(member.getSocialId().substring(0,5)+member.getId())
                .socialId(member.getSocialId())
                .authority(member.getAuthority())
                .socialEmail(member.getEmail())
                .profileImage(member.getProfileImage())
                .nickname(member.getNickname())
                .build();
    }
}
