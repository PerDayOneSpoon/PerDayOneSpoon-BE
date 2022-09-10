package com.sparta.perdayonespoon.repository;

import com.sparta.perdayonespoon.domain.Member;
import com.sparta.perdayonespoon.domain.dto.request.MemberSearchCondition;
import com.sparta.perdayonespoon.domain.dto.request.TokenSearchCondition;
import com.sparta.perdayonespoon.domain.dto.response.MemberResponseDto;
import com.sparta.perdayonespoon.domain.dto.response.MemberSearchDto;

import java.util.List;

public interface MemberRepositoryCustom {

    List<MemberSearchDto> getMember(MemberSearchCondition condition);
}
