package com.example.mpa_login.security.config;

import com.example.mpa_login.security.handler.CustomLoginFailureHandler;
import com.example.mpa_login.security.handler.CustomLoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
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

    private final OAuth2UserService customOAuth2UserService; // OAuth2 로그인 사용자 정보 처리 서비스

    // 비밀번호 암호화를 위한 Bean 등록
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCrypt 알고리즘을 사용한 PasswordEncoder 반환
    }

    // SecurityFilterChain: 보안 설정의 핵심 구성 요소
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // URL별 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/users/register", "/login", "/css/**", "/js/**").permitAll() // 누구나 접근 허용
                        .anyRequest().authenticated() // 그 외의 요청은 인증 필요
                )
                // OAuth2 로그인 설정
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login") // OAuth2 로그인도 동일한 로그인 페이지 사용
                        .successHandler(authenticationSuccessHandler()) // 로그인 성공 시 실행할 핸들러
                        .failureHandler(authenticationFailureHandler()) // 로그인 실패 시 실행할 핸들러
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService) // 사용자 정보 처리를 위한 서비스 지정
                        )
                )
                // 폼 로그인 설정
//                .formLogin(form -> form
//                        .loginPage("/login") // 커스텀 로그인 페이지 경로
//                        .permitAll() // 로그인 페이지는 인증 없이 접근 가능
//                        .defaultSuccessUrl("/todos", true) // 로그인 성공 시 "/todos"로 리다이렉트(항상)
//                )
                // 로그아웃 설정
                .logout(logout -> logout
                        .logoutUrl("/logout") // 로그아웃 요청 URL
                        .logoutSuccessUrl("/login?logout") // 로그아웃 성공 시 이동할 URL
                        .permitAll() // 로그아웃은 인증 없이도 가능
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