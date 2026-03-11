package com.flog.fourcut_log.auth.service;

import com.flog.fourcut_log.auth.model.dto.OAuth2CustomUser;
import com.flog.fourcut_log.auth.model.dto.OAuth2UserInfo;
import com.flog.fourcut_log.user.entity.User;
import com.flog.fourcut_log.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // OAuth2 유저 정보 가져오기
        OAuth2User oAuth2User = super.loadUser(userRequest);
        // OAuth2User의 attribute
        Map<String, Object> originAttributes = oAuth2User.getAttributes();

        // OAuth2 서비스 id 가져오기 (naver, kakao)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // OAuth2UserInfo로 통일된 형태로 변환
        OAuth2UserInfo userInfo = OAuth2UserInfo.of(registrationId, originAttributes);

        // User 엔티티로 변환 후 UserService에게 사용자 저장/업데이트 위임
        User newUser = userInfo.toEntity();
        User savedUser = userService.saveOrUpdateOAuth2User(newUser);

        // 권한 생성
        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + savedUser.getRole())
        );

        // OAuth2CustomUser 반환 (Spring Security가 사용)
        return new OAuth2CustomUser(
                registrationId,      // OAuth2 제공자
                originAttributes,    // 원본 OAuth2 데이터
                authorities,         // 권한 목록
                savedUser.getEmail() // 사용자 이메일
        );

    }
}