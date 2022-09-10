package com.sparta.perdayonespoon.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.perdayonespoon.domain.Authority;
import com.sparta.perdayonespoon.domain.Image;
import com.sparta.perdayonespoon.domain.Member;
import com.sparta.perdayonespoon.domain.RefreshToken;
import com.sparta.perdayonespoon.auth.NaverProfile;
import com.sparta.perdayonespoon.domain.dto.OauthToken;
import com.sparta.perdayonespoon.domain.dto.response.MemberResponseDto;
import com.sparta.perdayonespoon.domain.dto.response.TokenDto;
import com.sparta.perdayonespoon.jwt.Principaldetail;
import com.sparta.perdayonespoon.jwt.TokenProvider;
import com.sparta.perdayonespoon.mapper.MemberMapper;
import com.sparta.perdayonespoon.repository.ImageRepository;
import com.sparta.perdayonespoon.repository.MemberRepository;
import com.sparta.perdayonespoon.repository.RefreshTokenRepository;
import com.sparta.perdayonespoon.util.GenerateHeader;
import com.sparta.perdayonespoon.util.GenerateMsg;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class NaverService {

    private final RefreshTokenRepository refreshTokenRepository;

    private final TokenProvider tokenProvider;

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final RestTemplate restTemplate;

    private final ImageRepository imageRepository;

    @Value("${spring.security.oauth2.client.provider.naver.tokenUri}")
    private String NAVER_SNS_LOGIN_URL;

    @Value("${spring.security.oauth2.client.registration.naver.clientId}")
    private String NAVER_SNS_CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.naver.redirectUri}")
    private String NAVER_SNS_CALLBACK_URL;

    @Value("${spring.security.oauth2.client.registration.naver.clientSecret}")
    private String NAVER_SNS_CLIENT_SECRET;

    @Value("${spring.security.oauth2.client.provider.naver.userInfoUri}")
    private String NAVER_SNS_User_URL;
    public ResponseEntity login(String code,String state) {

        OauthToken oauthToken = getAccessToken(code,state);

        Member member = saveUser(oauthToken.getAccess_token());
        // 토큰발급
        TokenDto tokenDto = generateToken(member);

        // 리턴할 헤더 제작
        HttpHeaders httpHeaders = GenerateHeader.getHttpHeaders(tokenDto);

        // 리턴할 바디 제작
        MemberResponseDto memberResponseDto = MemberMapper.INSTANCE.orderToDto(member);

        //리턴 바디 상태 코드 및 메세지 넣기
        memberResponseDto.setTwoField(GenerateMsg.getMsg(HttpStatus.OK.value(),"로그인이 성공하셨습니다."));

        return ResponseEntity.ok().headers(httpHeaders).body(memberResponseDto);
    }

    private OauthToken getAccessToken(String code,String state) {
            UriComponents builder = UriComponentsBuilder.fromHttpUrl(NAVER_SNS_LOGIN_URL)
                    .queryParam("grant_type", "authorization_code")
                    .queryParam("client_id", NAVER_SNS_CLIENT_ID)
                    .queryParam("code", code)
                    .queryParam("state", state)
                    .queryParam("client_secret", NAVER_SNS_CLIENT_SECRET)
                    .queryParam("redirect_uri", NAVER_SNS_CALLBACK_URL)
                    .build();
            ResponseEntity<String> resultEntity = restTemplate.postForEntity(builder.toUriString(), null, String.class);
            System.out.println(resultEntity);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            OauthToken oauthToken = null;
            try {
                oauthToken = objectMapper.readValue(resultEntity.getBody(), OauthToken.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return oauthToken; //(8)
    }

    private Member saveUser(String access_token) {
        NaverProfile profile = findProfile(access_token);
        //(2)
        Optional<Member> checkmember = memberRepository.findBySocialId(profile.getResponse().getId());
        //(3)
        if(checkmember.isEmpty()) {
            Member member = Member.builder()
                    .socialCode(UUID.randomUUID().toString().substring(0,5))
                    .socialId(profile.getResponse().getId())
                    .nickname(profile.getResponse().getName())
                    .email(profile.getResponse().getEmail())
                    .authority(Authority.ROLE_USER)
                    .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .build();
            memberRepository.save(member);
            Image image = Image.builder()
                    .ImgUrl(profile.getResponse().getProfile_image())
                    .build();
            image.setMember(member);
            imageRepository.save(image);
            return member;
        }

        return checkmember.get();
    }

    private NaverProfile findProfile(String token) {
        //(1-3)
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token); //(1-4)
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        //(1-5)
        HttpEntity<MultiValueMap<String, String>> googleProfileRequest =
                new HttpEntity<>(headers);

        //(1-6)
        // Http 요청 (POST 방식) 후, response 변수에 응답을 받음
        ResponseEntity<String>  naverProfileResponse = restTemplate.postForEntity(NAVER_SNS_User_URL,googleProfileRequest,String.class);

        //(1-7)
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        NaverProfile naverProfile = null;
        try {
            naverProfile = objectMapper.readValue(naverProfileResponse.getBody(), NaverProfile.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return naverProfile;
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
