package com.sparta.perdayonespoon.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Table(name = "member")
@Entity // RDS
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String Email;

    @JsonIgnore
    @Column(nullable = false, unique = true)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false, unique = true)
    private String socialId;

    @Column
    private String profileImage;

    @Enumerated(EnumType.STRING)
    private Authority authority;

    @Builder
    public Member(String email, String password, String nickname, String socialId,String profileImage, Authority authority) {
        this.Email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.socialId = socialId;
        this.authority = authority;
    }
}

