package com.sparta.perdayonespoon.domain;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sparta.perdayonespoon.domain.dto.S3Dto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Table(name = "image")
@Entity
public class Image extends  Timestamped{

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column
        private String ImgUrl;

        @Column
        private String ImgName;

        @OneToOne
        @JsonBackReference
        private Member member;

        @Builder
        public Image(String ImgUrl, String ImgName, Member member){
            this.ImgUrl=ImgUrl;
            this.ImgName=ImgName;
            this.member=member;
        }
        public void SetTwoField(S3Dto s3DTO){
            this.ImgName = s3DTO.getFileName();
            this.ImgUrl = s3DTO.getUploadImageUrl();
        }

        public void setMember(Member member){
            this.member = member;
            this.getMember().SetImage(this);
        }
}
