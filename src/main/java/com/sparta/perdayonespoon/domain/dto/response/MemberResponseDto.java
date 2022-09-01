package com.sparta.perdayonespoon.domain.dto.response;

import com.sparta.perdayonespoon.domain.Authority;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberResponseDto {
    private Long id;

    private String socialEmail;

    private String nickname;

    private String socialId;

    private String socialCode;

    private String profileImage;

    private Authority authority;
}
