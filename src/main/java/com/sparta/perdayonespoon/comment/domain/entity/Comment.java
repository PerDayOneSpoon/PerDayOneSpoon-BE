package com.sparta.perdayonespoon.comment.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sparta.perdayonespoon.domain.Goal;
import com.sparta.perdayonespoon.domain.Member;
;
import com.sparta.perdayonespoon.util.Timestamped;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "comment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    private Goal goal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "member_id")
    private Member member;

    @Column
    private String content;

    @Column
    private String profileImage;

    @Column
    private String nickname;

    @Column
    private String socialId;

    @Builder
    public Comment(Goal goal, Member member, String content,String profileImage,String nickname,String socialId){
        this.goal =goal;
        goal.getCommentList().add(this);
        this.member = member;
        this.content = content;
        this.profileImage = profileImage;
        this.nickname = nickname;
        this.socialId = socialId;
    }

    public void changeImageandName(String profileImage, String nickname){
        this.profileImage = profileImage;
        this.nickname = nickname;
    }

    public void changeName(String nickname) {
        this.nickname = nickname;
    }
}
