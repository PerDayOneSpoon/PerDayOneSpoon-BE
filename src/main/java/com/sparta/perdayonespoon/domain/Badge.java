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

    @Column(unique = true)
    private String badgeName;

    @Column(unique = true)
    private long badgeNumber;

    @Column
    private LocalDate createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder(builderClassName = "realBadge", builderMethodName = "realBadgeBuilder")
    public Badge(String badgeName,long badgeNumber, LocalDate createdAt,Member member){
        this.badgeName = badgeName;
        this.badgeNumber = badgeNumber;
        this.createdAt = createdAt;
        this.member=member;
        this.member.getBadgeList().add(this);
    }

    @Builder(builderClassName = "fakeBadge", builderMethodName = "fakeBadgeBuilder")
    public Badge(String badgeName){
        this.badgeName = badgeName;
    }
}
