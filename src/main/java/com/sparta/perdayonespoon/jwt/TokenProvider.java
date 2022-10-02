package com.sparta.perdayonespoon.jwt;


import com.sparta.perdayonespoon.domain.Authority;
import com.sparta.perdayonespoon.domain.Image;
import com.sparta.perdayonespoon.domain.Member;
import com.sparta.perdayonespoon.domain.dto.response.TokenDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TokenProvider {

    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_TYPE = "bearer";

    private static final String MEMBER_KEY = "member";

    private static final String MEMBER_IMAGE = "image";

    private static final String SOCIAL_ID = "socialid";

//    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30000;            // 20일

    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 60;            // 60분

//    private static final long ACCESS_TOKEN_EXPIRE_TIME = 45000L;            // 100초
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7;  // 7일
    private final Key key;

    public TokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public TokenDto generateTokenDto(Authentication authentication) {
        // 권한들 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Principaldetail principaldetail = (Principaldetail)authentication.getPrincipal();
        Member member = principaldetail.getMember();

        long now = new Date().getTime();

        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        String accessToken = Jwts.builder()
                .setSubject(String.valueOf(member.getId()))       // payload "sub": "name"
                .claim(SOCIAL_ID,member.getSocialId())
                .claim(MEMBER_KEY,member.getNickname())      // payload "member" : "member.getUserNic"
                .claim(MEMBER_IMAGE,member.getImage().getImgUrl())
                .claim(AUTHORITIES_KEY, authorities)        // payload "auth": "ROLE_USER"
                .setExpiration(accessTokenExpiresIn)        // payload "exp": 1516239022 (예시)
                .signWith(key, SignatureAlgorithm.HS512)    // header "alg": "HS512"
                .compact();

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS512)
                .setHeaderParam("JWT_HEADER_PARAM_TYPE","headerType")
                .compact();
//      return Jwts.builder()
//              .setHeaderParam("typ", BEARER_TYPE)
//              .claim(AUTHORITIES_KEY, authorities)
//              .setExpiration(accessTokenExpiresIn)
//              .compact();
        return TokenDto.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .refreshToken(refreshToken)
                .build();
    }

    // accessToken에서 인증 정보 조회
    // 토큰에 담겨 있는 정보를 이용해 Authentication 객체를 리턴하는 메소드이다.
    // createToken 과 정확히 반대의 역할을 해주는 메소드이다.
    // 토큰을 parameter 로 받아서 토큰으로 claim 을 만들고, 최종적으로는 Authentication 객체를 리턴한다.
    public Authentication getAuthentication(String accessToken) {

//        RedisUtil redisUtil = new RedisUtil();
//        redisUtil.setValues("accesstoken",accessToken, Duration.from((TemporalAmount) parseClaims(accessToken).getExpiration()));
//        // 토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        Authority authority;
        if(Authority.ROLE_USER.equals(claims.get(AUTHORITIES_KEY))){
            authority = Authority.ROLE_USER;
        } else {authority = Authority.ROLE_ADMIN;}
        Image image = Image.builder().ImgUrl(claims.get(MEMBER_IMAGE).toString()).build();
        Member member = Member.builder()
                .authority(authority)
                .id(Long.parseLong(claims.getSubject()))
                .image(image)
                .nickname(claims.get(MEMBER_KEY).toString())
                .socialId(claims.get(SOCIAL_ID).toString())
                .build();


        Principaldetail principaldetail = new Principaldetail(member);
        return new UsernamePasswordAuthenticationToken(principaldetail, "", authorities);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            throw new MalformedJwtException("잘못된 JWT 서명입니다.");
        } catch (UnsupportedJwtException e) {
            throw new UnsupportedJwtException("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("JWT 토큰이 잘못되었습니다.");
        } catch (ExpiredJwtException e){
            throw new ExpiredJwtException(Jwts.header(),Jwts.claims(),"만료된 토큰입니다");
        }
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
