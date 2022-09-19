package com.sparta.perdayonespoon.mapper;

import com.sparta.perdayonespoon.domain.Member;
import com.sparta.perdayonespoon.domain.dto.response.MemberResponseDto;
import com.sparta.perdayonespoon.domain.dto.response.MemberResponseDto.MemberResponseDtoBuilder;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-09-17T03:11:06+0900",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 11.0.15 (Azul Systems, Inc.)"
)
@Component
public class MemberMapperImpl implements MemberMapper {

    @Override
    public MemberResponseDto orderToDto(Member member) {
        if ( member == null ) {
            return null;
        }

        MemberResponseDtoBuilder memberResponseDto = MemberResponseDto.builder();

        memberResponseDto.id( member.getId() );
        memberResponseDto.nickname( member.getNickname() );
        memberResponseDto.socialId( member.getSocialId() );
        memberResponseDto.authority( member.getAuthority() );
        memberResponseDto.status( member.getStatus() );

        memberResponseDto.socialCode( member.getSocialCode() );
        memberResponseDto.profileImage( member.getImage().getImgUrl() );

        return memberResponseDto.build();
    }
}
