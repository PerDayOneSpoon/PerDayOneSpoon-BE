package com.sparta.perdayonespoon.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class Goal extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;
    @Column
    private String start_date;

    @Column
    private String end_date;

    @Column
    private String time;

    @Column
    private long category;

    @Column
    private long characterId;

    @Column
    private boolean privateCheck;

    @Column
    private String socialId;

    @Builder
    public Goal(String title, String start_date, String end_date, String time, long category,
                long characterId, boolean privateCheck, String socialId){
        this.title = title;
        this.category = category;
        this.start_date = start_date;
        this.end_date = end_date;
        this.characterId = characterId;
        this.privateCheck = privateCheck;
        this.time = time;
        this.socialId=socialId;
    }
}
