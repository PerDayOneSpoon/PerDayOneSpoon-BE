package com.sparta.perdayonespoon.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Friend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String followerId;

    @Column
    private String followingId;

    @Builder
    public Friend(String followerId, String followingId){
        this.followerId = followerId;
        this.followingId = followingId;
    }
}
