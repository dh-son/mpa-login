package com.example.mpa_login.todo;

import com.example.mpa_login.todo.model.Todo;
import com.example.mpa_login.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor // final 필드를 매개변수료 하는 생성자랄 Lombok이 자동 생성
@Service
public class TodoService {

    private final TodoRepository todoRepository;

    // 새로운 Todo를 추가하고 DB에 저장하는 메서드
    public Todo addTodo(Todo todo, User user) {
        todo.setUser(user);
        return todoRepository.save(todo);
    }

    // 특정 사용자의 Todo 목록을 조회하는 메서드
    public List<Todo> getTodosByUser(User user) {
        return todoRepository.findByUserId(user.getId());
    }

    // 특정 ID의 Todo를 삭제하는 메서드 (소유자 확인 포함)
    public void deleteTodoById(Long id, User user) {
        Todo todo = validatedTodo(id, user);

        // 검증 통과 시 삭제
        todoRepository.deleteById(id);
    }

    // ID로 Todo를 조회하면 Optional로 반환 (수정 폼 등에서 사용)
    public Optional<Todo> getTodoById(Long id) {
        return todoRepository.findById(id);
    }

    // 특정 ID의 Todo를 수정하는 메서드 (소유자 검증 포함)
    public void updateTodo(Long id, String title, String description, User user) {
        Todo todo = validatedTodo(id, user);

        // 제목과 설명을 수정하고 저장
        todo.setTitle(title);
        todo.setDescription(description);
        todoRepository.save(todo);
    }

    private Todo validatedTodo(Long id, User user) {
        // ID로 할 일을 조회, 없으면 예외 발생
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Todo not found"));

        // 현재 사용자와 해당 할 일의 소유자가 일치하지 않으면 보안 예외 발생
        if (!todo.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Unauthorized");
        }

        return todo;
    }
}
