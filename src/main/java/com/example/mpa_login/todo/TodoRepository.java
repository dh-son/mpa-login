package com.example.mpa_login.todo;

import com.example.mpa_login.todo.model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    // 특정 사용자 ID에 해당하는 할 일 목록을 조회하는 메서드
    List<Todo> findByUserId(Long userId); // user_id로 할 일 목록 조회
}
