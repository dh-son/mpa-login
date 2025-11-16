package com.example.mpa_login.security.config;

import com.example.mpa_login.security.handler.CustomLoginFailureHandler;
import com.example.mpa_login.security.handler.CustomLoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * spring security 설정의 시작점
 * - 인증, 인가, JWT 필터, OAuth 설정 등 포함
 */
@Configuration // 이 클래스가 설정 클래스임을 Spring에게 알림
@EnableWebSecurity // Spring Security 웹 보안 활성화
@RequiredArgsConstructor
public class SecurityConfig {

    // OAuth2 로그인 시 사용자 정보를 처리할 커스텀 서비스
    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> customOAuth2UserService;

    // 일반 로그인 시 사용자 정보를 로드할 커스텀 서비스
    private final UserDetailsService customUserDetailsService;

    // 비밀번호 암호화를 위한 Bean 등록
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCrypt 알고리즘을 사용한 PasswordEncoder 반환
    }

    // SecurityFilterChain: 보안 설정의 핵심 구성 요소
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 요청 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/users/register", "/login", "/css/**", "/js/**").permitAll() // 비로그인 접근 허용
                        .anyRequest().authenticated() // 나머지는 인증 필요
                )
                // 일반 로그인 처리 시 사용자 정보 조회 서비스 등록
                .userDetailsService(customUserDetailsService)
                // 폼 로그인 설정
                .formLogin(form -> form
                        .loginPage("/login") // 사용자 정의 로그인 페이지
                        .permitAll()  // 로그인 페이지는 모두 접근 허용
                        .defaultSuccessUrl("/todos", true) // 로그인 성공 후 기본 이동 경로
                        .successHandler(authenticationSuccessHandler())
                        .failureHandler(authenticationFailureHandler())
                )
                // OAuth2 로그인 설정
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login") // 소셜 로그인도 동일한 로그인 페이지 사용
                        .successHandler(authenticationSuccessHandler()) // 로그인 성공 시 실행할 핸들러
                        .failureHandler(authenticationFailureHandler()) // 로그인 실패 시 실행할 핸들러
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService) // 사용자 정보 처리를 위한 서비스 지정
                        )
                )
                // 로그아웃 설정
                .logout(logout -> logout
                        .logoutUrl("/logout") // 로그아웃 요청 URL
                        .logoutSuccessUrl("/login?logout") // 로그아웃 성공 시 이동할 URL
                        .permitAll() // 로그아웃 경로 접근 허용
                );

        return http.build(); // 설정 완료 후 SecurityFilterChain 반환
    }

    // 로그인 성공 시 실행될 핸들러 Bean 등록
    @Bean
    AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new CustomLoginSuccessHandler(); // 커스텀 로그인 성공 핸들러 반환
    }

    // 로그인 실패 시 실행될 핸들러 Bean 등록
    @Bean
    AuthenticationFailureHandler authenticationFailureHandler() {
        return new CustomLoginFailureHandler(); // 커스터 로그인 실패 핸들러 반환
    }
}