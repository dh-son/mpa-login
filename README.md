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
