package com.sparta.perdayonespoon.websocket.domain.repository;


import com.sparta.perdayonespoon.domain.Member;
import com.sparta.perdayonespoon.websocket.domain.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    List<Participant> findAllByMember(Member member);

}