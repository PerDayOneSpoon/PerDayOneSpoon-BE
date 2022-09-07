package com.sparta.perdayonespoon.repository;

import com.sparta.perdayonespoon.domain.Member;
import com.sparta.perdayonespoon.domain.dto.request.TokenSearchCondition;

import java.util.List;

public interface RefreshTokenRepositoryCustom {

    Member getMember(TokenSearchCondition condition);
}
