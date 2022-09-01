package com.sparta.perdayonespoon.domain.dto.request;

import lombok.Data;

@Data
public class GoogleOAuthToken {
    private String access_token;
    private int expires_in;
    private String scope;
    private String token_type;
    private String id_token;
}
