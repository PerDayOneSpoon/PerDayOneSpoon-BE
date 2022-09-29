package com.sparta.perdayonespoon.repository;

import com.sparta.perdayonespoon.domain.Member;
import com.sparta.perdayonespoon.domain.dto.request.MemberSearchCondition;
import com.sparta.perdayonespoon.domain.dto.request.TokenSearchCondition;
import com.sparta.perdayonespoon.domain.dto.response.MemberResponseDto;
import com.sparta.perdayonespoon.domain.dto.response.MemberSearchDto;
import com.sparta.perdayonespoon.domain.dto.response.MyPageCollectDto;

import java.util.List;
import java.util.Optional;

public interface MemberRepositoryCustom {

    List<MemberSearchDto> getMember(MemberSearchCondition condition, String socialId);

    MyPageCollectDto getMypageData(String socialId);

    Optional<Member> findByMemberId(Long id);
}
