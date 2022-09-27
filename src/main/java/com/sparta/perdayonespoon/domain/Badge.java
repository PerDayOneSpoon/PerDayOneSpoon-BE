package com.sparta.perdayonespoon.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "badge")
@Getter
public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String badgeName;

    @Column
    private long badgeNumber;

    @Column
    private LocalDate createdAt;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Badge(String badgeName,long badgeNumber, LocalDate createdAt,Member member){
        this.badgeName = badgeName;
        this.badgeNumber = badgeNumber;
        this.createdAt = createdAt;
        this.member=member;
        this.member.getBadgeList().add(this);
    }
}
