# MPA-로그인

## 1.일반 로그인 코드
- 엔티티 클래스 정의
  - Todo Entity: 할일 목록
  - User Entity: 사용자
- 리포지토리 인터페이스 정의
  - TodoRepository: 특정 사용자 ID에 해당하는 할 일 목록을 조회하는 메서드 추가
  - UserRepository: 사용자 이름으로 사용자 정보를 조회하는 메서드 추가
- 서비스 클래스 정의
  - TodoService: Todo 등록, 수정, 삭제, 조회
  - UserService: User 등록, 조회
- 인증된 사용자 정보 클래스 정의
  - CustomUserDetails: Spring Security에서 사용자 정보를 담는 custom UserDetails
- 로그인 인증 처리 클래스 정의
  - CustomUserDetailsService: 로그인 시 사용자 정보를 DB에서 조회하고 검증한 뒤 spring security가 이해할 수 있는 형태로 래핑해서 반환
    - 사용자가 로그인할 때 아이디(username)를 기반으로 사용자 정보를 불러오는 핵심 인터페이스
    - 로그인 시점에 spring security가 자동으로 이 클래스를 찾아서 loadUserByUsername() 메소드 호출
    - loadUserByUsername() return 값을 기반으로 사용자 인증 진행
    - 즉, 로그인 인증 흐름에 시작점
    - 어플리케이션의 사용자 정보를 spring security가 이해할 수 있는 형태로 변환
- 리스너와 핸들러 구현
  - AuthenticationEventListeners: spring security 인증 관련 이벤트들을 수신하여 처리
  - CustomLoginFailureHandler: 로그인 실패 시 자동 실행되는 커스텀 핸들러 클래스
  - CustomLoginSuccessHandler: 로그인 성공 시 자동 실행되는 커스텀 핸들러 클래스
- 스프링 시큐리티 설정
  - SecurityConfig: Spring security 설정의 시작점
    - PasswordEncoder: 비밀번호 암호화
    - SecurityFilterChain: 보안 설정의 핵심 구성 요소
    - AuthenticationSuccessHandler: 로그인 성공 시 실행될 핸들러
    - AuthenticationFailureHandler: 로그인 실패 시 실행될 핸들러
- 컨트롤러 클래스 정의
  - HomeController
  - TodoController: 할 일 목록 조회, 추가, 삭제, 수정
  - UserController: 회원 가입
- 타임리프웹화면생성
  - index.html
  - login.html
  - register.html
  - edit_todo.html
   
## 2.소셜로그인
- 소셜 로그인 코드
  - User: 소셜 로그인 유형, 소셜 로그인 ID 필드 추가
  - OAuthAttributes: OAuth 인증 후 반환된 사용자 정보를 담는 DTO 클래스
  - CustomOAuth2User: OAuth 로그인 사용자의 정보를 확장하여 커스텀 유저 객체로 만드는 클래스
                      소셜로그인 사용자를 위한 확장 클래스(서비스에서 필요로 하는 사용자 정보를 쉽게 활용)
  - CustomOAuth2UserService: 소셜 로그인 사용자 정보와 회원 가입 처리 클래스
  - SecurityConfig: OAuth2 로그인 설정
  - TodoController: OAuth 사용자 정보로 캐스팅 변경
  - login.html: 구글 로그인 버튼 추가
- 소셜 로그인 기능 동작 순서
  1. 로그인 페이지에 소셜 로그인 버튼 만들기: login.html
     - /oauth2/authorization/google 경로로 요청 발생
     - Spring Security 가 요청을 가로채서 구글 로그인 페이지로 전송
  2. Spring Security 에서 소셜 로그인 설정하기: SecurityConfig.java
     - OAuth2 로그인 설정
  3. 구글 OAuth 정보 설정: application.yml
     - OAuth2 클라이언트 정보를 기반으로 spring boot 는 구글과 자동으로 통신 구성
  4. 사용자 정보 매핑하기: OAuthAttributes.java
     - 구글에서 넘겨준 사용자 정보를 자바 객체로 변환해서 쉽게 사용할 수 있도록 도와줌
  5. 사용자 DB 저장 또는 조회 + 인증 객체 생성: CustomOAuth2UserService.java
     - 이메일 기준으로 유저를 찾고, 없으면 새로 등록
     - 인증을 위한 사용자 객체인 CustomOAuth2User 를 만들어서 반환
     - CustomOAuth2User 객체가 Spring Security 에 세션에 등록되어 사용
  6. 인증된 사용자 클래스 정의: CustomOAuth2User.java
     - 인증된 사용자 정보를 담고 있음
     - 이후 Controller 에서 authentication.getPrincipal() 로 정보 접근
  7. 로그인 성공 후 이동 경로 지정: CustomLoginSuccessHandler.java
  8. Controller 에서 인증된 사용자 정보 활용: TodoController.java
- 네이버 소셜 로그인
  - OAuthAttributes: Naver OAuth2 인증 후 반환된 사용자 정보를 담는 DTO 클래스
  - CustomOAuth2UserService: socialType = registrationId
  - login.html: Naver 로그인 버튼
- 카카오 소셜 로그인
  - OAuthAttributes: Kakao OAuth2 인증 후 반환된 사용자 정보를 담는 DTO 클래스
  - CustomOAuth2UserService: socialType = registrationId
  - login.html: Kakao 로그인 버튼
- 깃허브 소셜 로그인
  - OAuthAttributes: Github OAuth2 인증 후 반환된 사용자 정보를 담는 DTO 클래스
  - CustomOAuth2UserService: email null 인 경우 직접 조회
  - login.html: GitHub 로그인 버튼
- 통합 소셜 로그인
  - OAuthAttributes: 플랫폼에 따라 분기 처리하여 객체 생성
## 3.일반 로그인 + 소셜로그인
- 통합 사용자 객체 클래스
  - CustomUser: 일반 로그인과 OAuth2 로그인을 모두 처리할 수 있는 통합 사용자 인증 객체
                UserDetails, OAuth2User 인터페이스 구현
- 소설 로그인 사용자 정보 처리 클래스
  - CustomOAuth2UserService: 사용자 정보를 담은 CustomUser 객체 반환 