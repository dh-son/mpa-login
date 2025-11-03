package com.example.mpa_login.security.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

/**
 * spring security 인증 관련 이벤트들을 수신하여 처리하는 리스너 클래스
 * - 인증 성공과 실패에 따른 추가 처리
 * - 인증 관련 이벤트를 감지해서 로그를 적재 또는 추가 처리
 */
@Slf4j
@Component
public class AuthenticationEventListeners {

    // 모든 인증 이벤트의 공통 처리, 이벤트 타입에 따라 분기 처리
    @EventListener
    public void handleAuthenticationEvent(AbstractAuthenticationEvent event) {
        log.info("handleAuthenticationEvent" + event); // 인증 이벤트 발생 시 로그 출력
    }

    // 로그인 실패 이벤트 처리(잘못된 자격 증명)
    // 비밀번호 실패 시: 실패 로그 남기기, 실패 횟수 증가시키키, 일정 횟수 이상 실패시 계정 잠금 처리
    @EventListener
    public void handleBadCredentials(AuthenticationFailureBadCredentialsEvent event) {
        log.info("handleBadCredentials"); // 로그인 실패 시 로그 출력
    }

    // 로그인 성공 이벤트 처리
    // 마지막 로그인 시각 기록, 성공적으로 로그인한 사용자에게 알림 발송, 로그인 성공 횟수 초기화
    @EventListener
    public void handleAuthenticationSuccess(AuthenticationSuccessEvent event) {
        log.info("handleAuthenticationSuccess"); // 로그인 성공 시 로그 출력
    }
}
