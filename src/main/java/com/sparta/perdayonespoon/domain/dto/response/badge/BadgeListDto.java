package com.sparta.perdayonespoon.domain.dto.response.badge;

import com.sparta.perdayonespoon.domain.dto.response.MsgDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BadgeListDto {

    private List<BadgeResponseDto> badgeResponseDtoList;

    private long code;

    private String msg;

    @Builder
    public BadgeListDto(List<BadgeResponseDto>badgeResponseDtoList, MsgDto msgDto){
        this.badgeResponseDtoList=badgeResponseDtoList;
        this.code = msgDto.getCode();
        this.msg = msgDto.getMsg();
    }
}
