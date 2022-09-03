package com.sparta.perdayonespoon.mapper;

import com.sparta.perdayonespoon.domain.Member;
import com.sparta.perdayonespoon.domain.dto.response.MemberResponseDto;
import com.sparta.perdayonespoon.domain.dto.response.MemberResponseDto.MemberResponseDtoBuilder;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-09-03T16:18:05+0900",
    comments = "version: 1.4.2.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-7.5.jar, environment: Java 11.0.16 (Amazon.com Inc.)"
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
        memberResponseDto.profileImage( member.getProfileImage() );
        memberResponseDto.authority( member.getAuthority() );

        memberResponseDto.socialCode( member.getSocialCode()+member.getId() );

        return memberResponseDto.build();
    }
}
