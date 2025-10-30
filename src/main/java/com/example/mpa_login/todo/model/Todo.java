package com.example.mpa_login.todo.model;

import com.example.mpa_login.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "todos")
public class Todo {

    @Id // 기본 키(primary key)로 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 값을 DB에서 자동 생성 (Auto Increment)
    private Long id;

    private String title; // 할 일 제목
    private String description; // 할 일 설명
    private boolean completed; // 완료 여부

    // 다대일(N:1) 관계 매핑 - 하나의 사용자는 여러 개의 할 일을 가질 수 있음
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // FK로 user_id 컬럼을 사용하며, null을 허용하지 않음
    private User user;
}
