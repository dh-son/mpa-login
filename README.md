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
