package com.sparta.perdayonespoon.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

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
    private LocalDateTime start_date;

    @Column
    private LocalDateTime currentdate;

    @Column
    private LocalDateTime end_date;

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

    @Column
    private boolean achievementCheck;

    @Builder
    public Goal(String title, LocalDateTime start_date,LocalDateTime currentdate,LocalDateTime end_date, String time, long category,
                long characterId, boolean privateCheck, String socialId,boolean achievementCheck){
        this.title = title;
        this.category = category;
        this.start_date = start_date;
        this.currentdate = currentdate;
        this.end_date = end_date;
        this.characterId = characterId;
        this.privateCheck = privateCheck;
        this.time = time;
        this.socialId=socialId;
        this.achievementCheck=achievementCheck;
    }
}
