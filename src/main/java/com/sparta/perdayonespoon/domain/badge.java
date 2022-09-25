package com.sparta.perdayonespoon.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Entity
@Getter
public class badge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String badgeName;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;
}
