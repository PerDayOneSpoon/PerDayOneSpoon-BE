package com.sparta.perdayonespoon.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenDto {

    private String grantType;
    private String accessToken;
    private String refreshToken;
    private long accessTokenExpiresIn;

    @Builder
    public TokenDto(String grantType, String accessToken, String refreshToken, long accessTokenExpiresIn){
        this.grantType=grantType;
        this.accessToken=accessToken;
        this.refreshToken=refreshToken;
        this.accessTokenExpiresIn=accessTokenExpiresIn;
    }

    public void SetRefreshtoken(String refreshToken){
        this.refreshToken=refreshToken;
    }
}
