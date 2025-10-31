package com.example.mpa_login.security.model;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Spring Security에서 사용자 정보를 담는 커스텀 UserDetails 구현 클래스
 * - 로그인 후 인증된 사용자 정보를 UserDetails로 관리
 */
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    // 직렬화: 객체를 바이트 단위로 변환해서 파일이나 네트워크로 전송할 수 있도록 만드는 과정
    //        세션저장, 캐싱 같은 기능에서 사용
    // Serializable 를 구현한 클래스는 이 값을 명시: 버전 충돌없이 안정적으로 직렬화 처리
    private static final long serialVersionUID = 1L; // 직렬화 버전 UID

    private final Long userId;
    private final String username;
    private final String password;

    // 사용자의 권한 목록 (ROLE_USER, ROLE_ADMIN 등): 인가 처리
    private final Collection<? extends GrantedAuthority> authorities;

    // 사용자의 권한 정보 반환: 필수
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    // 사용자의 비밀번호 반환: 필수
    @Override
    public String getPassword() {
        return password;
    }

    // 사용자의 이름 반환: 필수
    @Override
    public String getUsername() {
        return username;
    }

    // 커스텀 메서드: 사용자 ID 반환 - 비즈니스 로직에도 사용자 정보를 쓰기 위해 확정
    public Long getUserId() {
        return userId;
    }

    /**
    // 계정 만료 여부 (true = 만료되지 않음)
    @Override
    public boolean isAccountNonExpired() {
        return true; // 항상 true로 설정하여 만료 검사 생략
    }

    // 계정 잠김 여부 (true = 잠기지 않음)
    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정 잠금 로직을 사용하지 않으므로 true
    }

    // 자격 증명(비밀번호) 만료 여부
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 비밀번호 만료 검사 생략
    }

    // 계정 활성화 여부: 계정 정지, 탈퇴 시 사용
    @Override
    public boolean isEnabled() {
        return true; // 계정이 항상 활성화된 상태로 간주
    }
    */
}
