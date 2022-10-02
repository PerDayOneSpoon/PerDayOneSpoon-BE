package com.sparta.perdayonespoon.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

//어플리케이션 구동시 제일 먼저 시행되어 /**로 들어오는 url를 잡아 config등록에 따라 cors를 제일 앞단에서 검증
@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
//        config.addAllowedOriginPattern("*");
        //테스트용 서버
        config.addAllowedOrigin("http://localhost:3000/**");
        //실서버
        config.addAllowedOrigin("https://www.perday-onespoon.com/**");
        config.setAllowCredentials(true);
        //모든 메소드에 관해 허용해주는것
        config.addAllowedMethod("*");
        //모든 헤더에 관해 허용해주는것
        config.addAllowedHeader("*");
        // POST(Simple Request) 요청은 auto-generated Header 값 (= 기본 Header들) 말고는 access 불가
        // 따라서 추가 Header들을 Client가 볼 수 있도록 설정해야 함
        config.addExposedHeader("Authorization");
        config.addExposedHeader("RefreshToken");
        config.addExposedHeader("Access-Token-Expire-Time");
        //preflight-request가 최초로 응답 cors를 인증해주는 시간 5초간 예비요청오기전에 안전하다고 인증
        config.setMaxAge(5000L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}