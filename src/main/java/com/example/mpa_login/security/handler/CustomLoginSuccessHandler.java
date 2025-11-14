package com.example.mpa_login.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

/**
 * 로그인 성공 시 실행되는 커스텀 핸들러 클래스
 * - 로그인 성공 이후 어떤 후속 작업을 할지 정의
 * - 마지막 로그인 시간을 DB에 기록, 특정 사용자 권한에 따라 다른 페이지로 리다이렉트
 * - 토큰(JWT)을 발급해서 응답에 포함
 */
@Slf4j
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    // 로그인 성공 시 자동 호출되는 메서드
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, // 클라이언트 요청 객체
                                        HttpServletResponse response, // 서버 응답 객체
                                        Authentication authentication // 인증된 사용자 정보
    ) throws IOException, ServletException {

        log.info("onAuthenticationSuccess"); // 성공 로그 출력

        String targetUrl = "/todos"; // 로그인 성공 후 이동할 기본 URL 설정
        response.sendRedirect(targetUrl); // 해당 URL로 리다이렉트
    }
}
