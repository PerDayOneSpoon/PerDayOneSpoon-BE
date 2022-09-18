package com.sparta.perdayonespoon.domain;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sparta.perdayonespoon.domain.dto.S3Dto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "image")
@Entity
public class Image extends  Timestamped{

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column
        private String imgUrl;

        @Column
        private String imgName;

        @OneToOne
        @JsonBackReference
        private Member member;

        @Builder
        public Image(String ImgUrl, String ImgName, Member member){
            this.imgUrl=ImgUrl;
            this.imgName=ImgName;
            this.member=member;
        }
        public void SetTwoField(S3Dto s3DTO){
            this.imgName = s3DTO.getFileName();
            this.imgUrl = s3DTO.getUploadImageUrl();
        }

        public void setMember(Member member){
            this.member = member;
            this.getMember().SetImage(this);
        }
}
