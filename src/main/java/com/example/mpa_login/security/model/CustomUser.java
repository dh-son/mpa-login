package com.example.mpa_login.security.model;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

/**
 * 일반 로그인과 OAuth2 로그인을 모두 처리할 수 있는 통합 사용자 인증 객체
 * UserDetails: 일반 사용자 로그인 정보 (id, password) 제공
 * OAuth2User: OAuth2 로그인 사용자 정보 제공
 * 사용 예시: 로그인 후 컨트롤러에서 사용자 정보를 가져올 때
 *          @AuthenticationPrincipal CustomUser customUser
 */
@Getter
public class CustomUser implements UserDetails, OAuth2User {

    private static final long serialVersionUID = 1L;

    private Long userId; // DB의 사용자 고유 ID
    private String username; // 사용자명 (email로 사용)
    private String password; // 일반 로그인 사용자만 사용 (OAuth2는 null)
    private Collection<? extends GrantedAuthority> authorities; // 사용자 권한 목록
    private Map<String, Object> attributes; // OAuth2에서 전달된 사용자 정보

    // 일반 로그인 사용자를 위한 생성자
    public CustomUser(Long userId, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.attributes = null;
    }

    // OAuth2 로그인 사용자를 위한 생성자
    public CustomUser(Long userId, String username, Collection<? extends GrantedAuthority> authorities, OAuthAttributes oAuthAttributes) {
        this.userId = userId;
        this.username = username;
        this.authorities = authorities;
        this.attributes = oAuthAttributes.getAttributes();
        this.password = null;
    }

    @Override
    public String getName() {
        return username; // OAuth2User에서 사용하는 고유 사용자명 반환 (Spring Security용)
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes; // OAuth2 사용자 정보 반환
    }

    @Override
    public String getUsername() {
        return username; // UserDetails 인터페이스 구현: 사용자명(email) 반환
    }

    @Override
    public String getPassword() {
        return password; // 일반 로그인은 암호 반환, OAuth2는 null
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities; // 권한 리스트 반환
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정이 만료되지 않았음을 명시
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정이 잠겨 있지 않음을 명시
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 자격 증명이 만료되지 않았음을 명시
    }

    @Override
    public boolean isEnabled() {
        return true; // 계저이 활성화되어 있음을 명시
    }
}
