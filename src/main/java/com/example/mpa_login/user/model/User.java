package com.example.mpa_login.user.model;

import com.example.mpa_login.todo.model.Todo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 사용자 고유 ID

    private String username; // 사용자 이름 (또는 이메일 등 로그인 식별자)
    private String password; // 사용자 비밀번호 (암호화: BCryptPasswordEncoder 등)

    // 사용자와 할 일 간의 1:N 관계 매핑
    // mappedBy: Todo 엔티티의 "user" 필드를 기준으로 관계를 설정함
    @OneToMany(mappedBy = "user")
    private Set<Todo> todos; // 사용자가 소유한 할 일 목록
}
