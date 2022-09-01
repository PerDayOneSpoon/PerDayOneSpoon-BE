package com.sparta.perdayonespoon.auth;

import com.sparta.perdayonespoon.domain.OAuth2UserInfo;
import com.sparta.perdayonespoon.domain.ProviderType;

import java.util.Map;
//
//public class OAuth2UserInfoFactory {
//    public static OAuth2UserInfo getOAuth2UserInfo(ProviderType providerType, Map<String, Object> attributes) {
//        switch (providerType) {
//            case GOOGLE: return new GoogleOAuth2UserInfo(attributes);
//            case FACEBOOK: return new FacebookOAuth2UserInfo(attributes);
//            case NAVER: return new NaverOAuth2UserInfo(attributes);
//            case KAKAO: return new KakaoOAuth2UserInfo(attributes);
//            default: throw new IllegalArgumentException("Invalid Provider Type.");
//        }
//    }
//}