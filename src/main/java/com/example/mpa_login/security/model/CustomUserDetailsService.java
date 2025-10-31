package com.example.mpa_login.security.model;

import com.example.mpa_login.user.UserRepository;
import com.example.mpa_login.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Spring Security에서 사용자 인증 정보를 불러오기 위한 핵심 서비스 클래스
 * - 사용자가 로그인할 때 아이디(username)를 기반으로 사용자 정보를 불러오는 핵심 인터페이스
 * - 로그인 시점에 spring security가 자동으로 이 클래스를 찾아서 loadUserByUsername() 메소드 호출
 * - loadUserByUsername() return 값을 기반으로 사용자 인증 진행
 * - 즉, 로그인 인증 흐름에 시작점
 * - 어플리케이션의 사용자 정보를 spring security가 이해할 수 있는 형태로 변환
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // username을 기준으로 사용자 정보를 로딩하는 메서드
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("CustomUserDetailsService loadUserByUsername");

        // username으로 사용자 정보를 조회, 없으면 예외 발생
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // 사용자 정보 추출
        Long userId = user.getId();
        String email = user.getUsername();
        String password = user.getPassword();

        // 권한 리스트 생성 및 "ROLE_USER" 권한 추가
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER"); // 기본 권한 부여
        authorities.add(authority);

        // 사용자 정보를 담은 CustomUserDetails 객체 생성
        // 인증이 필요한 API에서 @AuthenticationPrincipal로 꺼내서 활용
        return new CustomUserDetails(userId, email, password, authorities);
    }
}
