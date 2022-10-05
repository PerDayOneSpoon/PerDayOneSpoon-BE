package com.sparta.perdayonespoon.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "heart")
public class Heart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String socialId;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "goal_id")
    private Goal goal;

    @Builder
    public Heart(Goal goal, String socialId){
        this.goal=goal;
        this.socialId = socialId;
        goal.getHeartList().add(this);
    }
}
