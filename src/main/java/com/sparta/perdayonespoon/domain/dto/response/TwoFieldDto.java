package com.sparta.perdayonespoon.domain.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import com.sparta.perdayonespoon.domain.Member;
import com.sparta.perdayonespoon.domain.RefreshToken;
import lombok.Data;

@Data
public class TwoFieldDto {

    private Member member;
    private RefreshToken refreshToken;

    @QueryProjection
    public TwoFieldDto(Member member, RefreshToken refreshToken){
        this.member = member;
        this.refreshToken=refreshToken;
    }
}
