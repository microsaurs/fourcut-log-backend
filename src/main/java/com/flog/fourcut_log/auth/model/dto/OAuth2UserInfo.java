package com.flog.fourcut_log.auth.model.dto;

import com.flog.fourcut_log.user.entity.User;
import lombok.Builder;

import java.util.Map;

public class OAuth2UserInfo {

    private final Map<String, Object> attributes;  // 원본 OAuth2 반환 정보
    private final String nameAttributesKey;        // OAuth2 식별자 키 (sub, id 등)
    private final String provider;                 // OAuth2 제공자 (kakao, naver)
    private final String name;                     // 사용자 이름
    private final String email;                    // 이메일

    @Builder
    public OAuth2UserInfo(Map<String, Object> attributes,
                          String nameAttributesKey,
                          String provider,
                          String name,
                          String email) {
        this.attributes = attributes;
        this.nameAttributesKey = nameAttributesKey;
        this.provider = provider;
        this.name = name;
        this.email = email;
    }

    public static OAuth2UserInfo of(String registrationId, Map<String, Object> attributes) {
        switch (registrationId.toLowerCase()) {
//            case "google":
//                return ofGoogle("sub", attributes);
            case "kakao":
                return ofKakao("id", attributes);
            case "naver":
                return ofNaver("id", attributes);
            default:
                throw new IllegalArgumentException("ILLEGAL_REGISTRATION_ID: " + registrationId);
        }
    }

    /**
     * Kakao OAuth2 사용자 정보 생성
     */
    private static OAuth2UserInfo ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");

        return OAuth2UserInfo.builder()
                .provider("kakao")
                .name(String.valueOf(kakaoProfile.get("nickname")))
                .email(String.valueOf(kakaoAccount.get("email")))
                .attributes(attributes)
                .nameAttributesKey(userNameAttributeName)
                .build();
    }

    /**
     * Naver OAuth2 사용자 정보 생성
     */
    private static OAuth2UserInfo ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuth2UserInfo.builder()
                .provider("naver")
                .name(String.valueOf(response.get("nickname")))
                .email(String.valueOf(response.get("email")))
                .attributes(response)  // 네이버는 response 내부 데이터를 저장
                .nameAttributesKey(userNameAttributeName)
                .build();
    }

    /**
     * User 엔티티로 변환
     */
    public User toEntity() {
        return User.builder()
                .nickname(name)
                .email(email)
                .lginType(provider)
                .role("USER")
                .build();
    }
}
