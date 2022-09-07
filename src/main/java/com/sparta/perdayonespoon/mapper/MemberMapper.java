package com.sparta.perdayonespoon.mapper;

import com.sparta.perdayonespoon.domain.Member;
import com.sparta.perdayonespoon.domain.dto.response.MemberResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberMapper {
    MemberMapper INSTANCE = Mappers.getMapper(MemberMapper.class); // 2

    @Mapping(target = "socialCode", expression = "java(member.getSocialId()+member.getId())") // 4
    @Mapping(target = "profileImage", expression = "java(member.getImage().getImgUrl())")
    MemberResponseDto orderToDto(Member member);
}
