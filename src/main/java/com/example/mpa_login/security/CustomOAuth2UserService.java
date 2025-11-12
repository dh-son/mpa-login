package com.example.mpa_login.security;

import com.example.mpa_login.security.model.CustomOAuth2User;
import com.example.mpa_login.security.model.OAuthAttributes;
import com.example.mpa_login.user.UserRepository;
import com.example.mpa_login.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * OAuth2 로그인 후 사용자 정보를 처리하는 커스텀 OAuth2UserService 구현 클래스
 * 1.외부 인증 제공자에서 사용자 정보를 수신
 * 2.받은 사용자 정보를 우리가 만든 OAuthAttributes로 변환
 * 3.기존 사용자인지 확인하고 없으면 회원가입을 진행
 * 4.사용자 정보를 담은 CustomOAuth2User 객체를 생성해서 반환
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    // SecurityConfig에서 주입받는 대시 직접 생성하여 사용 (순환참조 방지)
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // OAuth2 로그인 시 사용자 정보를 불러오고 처리하는 메서드
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("loadUser"); // 메서드 진입 로그

        // 기본 OAuth2UserService를 통해 사용자 정보 조회
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // OAuth2 서비스 등록 ID (ex: google, kakao 등)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // 사용자 식별 키 (ex: sub, id)
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();
        log.info("loadUser registrationId = " + registrationId);
        log.info("loadUser userNameAttributeName = " + userNameAttributeName);

        // OAuthAttributes 객체로 사용자 정보 매핑
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        // 속성 정보 추출
        String nameAttributeKey = attributes.getNameAttributeKey(); // 사용자 식별 키 이름
        String name = attributes.getName(); // 사용자 이름
        String email = attributes.getEmail();
        String picture = attributes.getPicture();
        String id = attributes.getId();
        String socialType = "google"; // 현재는 Google만 처리

        log.info("loadUser nameAttributeKey = " + nameAttributeKey);
        log.info("loadUser name = " + name);
        log.info("loadUser email = " + email);
        log.info("loadUser picture = " + picture);
        log.info("loadUser id = " + id);
        log.info("loadUser socialType = " + socialType);

        // null 방지를 위한 기본값 처리
        if (name == null) name = "";
        if (email == null) email = "";

        // 기본 권한 부여
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER"); // 일반 사용자 권한
        authorities.add(authority);

        // 이메일로 사용자 존재 여부 조회
        Optional<User> optionalUser = userRepository.findByUsername(email);

        User createdUser = null;

        // 존재하지 않으면 새 사용자 생성 및 저장
        if (optionalUser.isEmpty()) {
            User user = new User();
            user.setUsername(email);
            user.setPassword("1234"); // 기본 비밀번호 설정 (임시 값)
            user.setSocialId(id);
            user.setSocialType(socialType);

            createdUser = userRepository.save(user);
        } else { // 존재하면 해당 사용자 사용
            createdUser = optionalUser.orElseThrow(); // Optional에서 User 꺼냄
        }

        Long userId = createdUser.getId(); // DB에 저장된 사용자 ID

        // Custom OAuth2User 객체 반환: Spring Security에서 세션에 저장됨(Spring Security의 인증 컨텍스트에 등록)
        // Authentication.getPrincipal() 등을 통해 사용자 정보 확인
        return new CustomOAuth2User(userId, email, name, authorities, attributes);
    }
}
