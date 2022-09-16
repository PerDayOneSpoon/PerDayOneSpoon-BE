package com.sparta.perdayonespoon.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class Friend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String followId;

    @Column
    private String followingId;

    @Column
    private boolean follow;

    @Builder
    public Friend(String followId, String followingId){
        this.followId = followId;
        this.followingId =followingId;
    }
}
