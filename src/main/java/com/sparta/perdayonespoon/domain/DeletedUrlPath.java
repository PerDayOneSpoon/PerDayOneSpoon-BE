package com.sparta.perdayonespoon.domain;

import lombok.*;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class DeletedUrlPath {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column
    private String deletedUrlPath;

    @Builder
    public DeletedUrlPath(String deletedUrlPath){
        this.deletedUrlPath=deletedUrlPath;
    }

}