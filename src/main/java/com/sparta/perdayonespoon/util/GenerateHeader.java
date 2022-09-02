package com.sparta.perdayonespoon.util;

import com.sparta.perdayonespoon.domain.dto.response.TokenDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;

@Getter
@NoArgsConstructor
public class GenerateHeader {
    private static final HttpHeaders httpHeaders = new HttpHeaders();

    public static HttpHeaders getHttpHeaders(TokenDto tokenDto){
        httpHeaders.set("Authorization", "Bearer " + tokenDto.getAccessToken());
        httpHeaders.set("RefreshToken", tokenDto.getRefreshToken());
        httpHeaders.set("Access-Token-Expire-Time", String.valueOf(tokenDto.getAccessTokenExpiresIn()));
        return httpHeaders;
    }
}
