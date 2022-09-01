package com.sparta.perdayonespoon.domain.auth;

import lombok.Data;

@Data
public class GoogleProfile {
    private String sub;
    private String name;
    private String picture;
    private String email;
    private boolean email_verified;
}
