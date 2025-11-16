package com.example.mpa_login.security;

import com.example.mpa_login.security.model.CustomOAuth2User;
import com.example.mpa_login.security.model.CustomUser;
import com.example.mpa_login.security.model.OAuthAttributes;
import com.example.mpa_login.user.UserRepository;
import com.example.mpa_login.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    // SecurityConfig에서 주입받는 대신 직접 생성하여 사용 (순환참조 방지)
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // OAuth2 로그인 성공 시 사용자 정보를 로드하고 사용자 등록 또는 조회 및 반환
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("loadUser"); // 메서드 진입 로그

        // Spring Security 기본 OAuth2 사용자 서비스
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 현재 로그인 중인 소셜 플랫폼 (ex: google, kakao 등)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // 사용자 식별 키 (ex: sub, id)
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        log.info("loadUser registrationId = " + registrationId);
        log.info("loadUser userNameAttributeName = " + userNameAttributeName);

        // 각 소셜 플랫폼별 사용자 정보 파싱
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        // 속성 정보 추출
        String nameAttributeKey = attributes.getNameAttributeKey(); // 사용자 식별 키 이름
        String name = attributes.getName(); // 사용자 이름
        String email = attributes.getEmail();
        String picture = attributes.getPicture();
        String id = attributes.getId();
        String socialType = registrationId;

        // GitHub email 조회
        if (registrationId.equals("github") && email == null) {
            log.info("loadUser userRequest.getAccessToken().getTokenValue = " + userRequest.getAccessToken().getTokenValue());

            // 이메일 직접조회
            email = getEmailFromGitHub(userRequest.getAccessToken().getTokenValue());

            log.info("loadUser GitHub email = " + email);
        }

        log.info("loadUser nameAttributeKey = " + nameAttributeKey);
        log.info("loadUser name = " + name);
        log.info("loadUser email = " + email);
        log.info("loadUser picture = " + picture);
        log.info("loadUser id = " + id);
        log.info("loadUser socialType = " + socialType);

        // null 방지를 위한 기본값 처리
        if (name == null) name = "";
        if (email == null) email = "";

        // 사용자 권한 설정 (기본 ROLE_USER)
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER"); // 일반 사용자 권한
        authorities.add(authority);

        // 이메일 기준으로 사용자 조회
        Optional<User> optionalUser = userRepository.findByUsername(email);

        User createdUser = null;

        // 존재하지 않으면 새 사용자 생성 및 저장
        if (optionalUser.isEmpty()) {
            User user = new User();
            user.setUsername(email);
            user.setPassword(passwordEncoder.encode("1234")); // 기본 비밀번호 설정 (임시 값)
            user.setSocialId(id);
            user.setSocialType(socialType);

            createdUser = userRepository.save(user);
        } else { // 존재하면 해당 사용자 사용
            createdUser = optionalUser.orElseThrow(); // Optional에서 User 꺼냄
        }

        Long userId = createdUser.getId(); // DB 사용자 ID

        // 사용자 정보를 담은 CustomUser 객체 반환: Spring Security에서 세션에 저장됨(Spring Security의 인증 컨텍스트에 등록)
        // Controller에서 @AuthenticationPrincipal or Authentication.getPrincipal() 등을 통해 사용자 정보 확인
        return new CustomUser(userId, email, authorities, attributes);
    }

    // GitHub API를 통해 사용자의 이메일을 직접 가져오는 메서드
    private String getEmailFromGitHub(String accessToken) {
        String url = "https://api.github.com/user/emails";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("Accept", "application/vnd.github.v3+json");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // 이메일 정보 요청
        ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, entity, List.class);

        List<Map<String, Object>> emails = response.getBody();

        // primary 이메일 추출
        if (emails != null) {
            for (Map<String, Object> emailData : emails) {
                if ((Boolean) emailData.get("primary")) {
                    return (String) emailData.get("email");
                }
            }
        }

        return null;
    }
}
