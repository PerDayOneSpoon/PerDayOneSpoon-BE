package com.sparta.perdayonespoon.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class Heart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String socialId;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "Goal_Id")
    private Goal goal;

    @Builder
    public Heart(Goal goal, String socialId){
        this.goal=goal;
        this.socialId = socialId;
        goal.getHeartList().add(this);
    }
}
