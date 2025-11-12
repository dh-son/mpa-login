package com.example.mpa_login.security.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;

/**
 * OAuth 로그인 처리 후 OAuth2UserService에서 CustomOAuth2User class 생성
 OAuthAttributes attributes = OAuthAttributes.of(...); // 사용자 정보 매핑
 CustomOAuth2User user - new CustomOAuth2User (
 userId, email, nickname, authorities, attributes)
 */

public class CustomOAuth2User extends DefaultOAuth2User {

    private static final long serialVersionID = 1L; // 직렬화를 위한 UID

    // 내부에서 관리하는 사용자 정보
    private Long userId; // 사용자 ID (DB의 User ID와 연동)
    private String username; // 사용자 이름 또는 이메일
    private String nickname; // 사용자 닉네임

    // 커스텀 OAuth2 사용자 객체 생성자
    public CustomOAuth2User(Long userId,
                            String username,
                            String nickname,
                            Collection<? extends GrantedAuthority> authorities, // 권한목록
                            OAuthAttributes attributes // OAuth로부터 전달받은 속성 정보
    ) {
        // 부모클래스인 DefaultOauth2User의 생성자 호출
        // getAttributes(): 사용자 정보 map, getNameAttributeKey(): 식별키
        super(authorities, attributes.getAttributes(), attributes.getNameAttributeKey());
        this.userId = userId;
        this.username = username;
        this.nickname = nickname;
    }

    @Override
    public String getName() {
        return username;
    }

    public String getUsername() {
        return username;
    }

    public String getNickname() {
        return nickname;
    }

    public Long getUserId() {
        return userId;
    }
}

