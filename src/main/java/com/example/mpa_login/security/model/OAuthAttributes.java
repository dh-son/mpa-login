package com.example.mpa_login.security.model;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
// Oauth 인증 후 반환된 사용자 정보를 담는 DTO 클래스
public class OAuthAttributes {

    private Map<String, Object> attributes; // OAuth 제공자로부터 전달받은 사용자 정보 전체 Map
    private String nameAttributeKey; // OAuth 사용자 식별 키 (예:sub, id)
    private String name; // 사용자 이름
    private String email; // 사용자 이메일
    private String picture; // 사용자 프로필 사진
    private String id; // 사용자 고유 ID

    // Builder를 통한 생성자
    @Builder
    public OAuthAttributes(Map<String, Object> attributes,
                           String nameAttributeKey,
                           String name,
                           String email,
                           String picture,
                           String id) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.id = id;
    }

    // OAuthAttributes 객체를 생성하는 정적 팩토리 모드
    // 현재는 Google만 처리하며 추후 다른 플랫폼에 따라 분기 가능
    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        return ofGoogle(userNameAttributeName, attributes); // Google 로그인 처리
    }

    // Google 로그인 전용 사용자 정보 매핑 메서드
    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("picture"))
                .id((String) attributes.get("userNameAttributeName"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }
}
