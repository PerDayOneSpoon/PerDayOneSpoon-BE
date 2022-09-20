package com.sparta.perdayonespoon.util;

import com.sparta.perdayonespoon.domain.SuccessMsg;
import com.sparta.perdayonespoon.domain.dto.request.TokenSearchCondition;
import com.sparta.perdayonespoon.domain.dto.response.TokenDto;
import com.sparta.perdayonespoon.domain.dto.response.TwoFieldDto;
import com.sparta.perdayonespoon.jwt.Principaldetail;
import com.sparta.perdayonespoon.jwt.TokenProvider;
import com.sparta.perdayonespoon.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReissueUtil {
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenProvider tokenProvider;

    public ResponseEntity regenerateToken(TokenSearchCondition condition){
        if(tokenProvider.validateToken(condition.getRefreshtoken())){
            TwoFieldDto twoFieldDto = refreshTokenRepository.getMember(condition);
            Principaldetail principaldetail = new Principaldetail(twoFieldDto.getMember());
            Authentication authentication = new UsernamePasswordAuthenticationToken(principaldetail, null, principaldetail.getAuthorities());
            TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + tokenDto.getAccessToken());
            headers.set("RefreshToken", twoFieldDto.getRefreshToken().getValue());
            headers.set("Access-Token-Expire-Time", String.valueOf(tokenDto.getAccessTokenExpiresIn()));
            return ResponseEntity.ok().headers(headers).body(GenerateMsg.getMsg(SuccessMsg.RE_GENERATE_TOKEN.getCode(), SuccessMsg.RE_GENERATE_TOKEN.getMsg()));
        }
        else
            throw new IllegalArgumentException("리프레쉬 토큰이 유효하지 않습니다.");
    }
}
