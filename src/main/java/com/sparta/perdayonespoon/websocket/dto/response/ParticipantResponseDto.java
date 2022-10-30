package com.sparta.perdayonespoon.websocket.dto.response;


import com.sparta.perdayonespoon.domain.dto.response.MemberResponseDto;
import com.sparta.perdayonespoon.mapper.MemberMapper;
import com.sparta.perdayonespoon.websocket.domain.entity.Participant;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParticipantResponseDto {

    private MemberResponseDto memberResponseDto;

    @Builder
    public ParticipantResponseDto(Participant participant) {
        memberResponseDto = MemberMapper.INSTANCE.orderToDto(participant.getMember());
    }
}