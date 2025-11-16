package com.example.mpa_login.todo;

import com.example.mpa_login.security.model.CustomUser;
import com.example.mpa_login.todo.model.Todo;
import com.example.mpa_login.user.UserService;
import com.example.mpa_login.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 할 일(Todo) 목록과 관련된 웹 요청을 처리하는 컨트롤러
 * # 보안 및 인증 처리 방식 요약
 * - Authentication 객체에서 로그인된 사용자 정보를 꺼냄
 * - OAuth2 로그인 사용자의 경우 CustomOAuth2User 로 캐스팅
 * - 사용자 ID로 할 일 소유자와 비교하여 권한이 있는 사용자만 조작 가능
 * # 연계 흐름 예시
 * 1. 사용자가 로그인(OAuth2) -> CustomOAuthUser 로 인증 정보 저장
 * 2. 사용자가 /todos 접근 -> 해당 사용자에게 연결된 Todo 목록 조회
 * 3. 할 일 추가/삭제/수정 -> 사용자 ID로 검증 후 조작
 */

/**
 * 로그인한 사용자 정보를 처리하는 방식
 * Object principal = authentication.getPrincipal();
 *
 * public String myPage(@AuthenticationPrincipal CustomUser customUser) {
 *     // customUser.getUsername() 등으로 바로 사용 가능
 * }
 */

@RequiredArgsConstructor
@Controller
@RequestMapping("/todos") // /todos 경로 이하의 요청을 처리
public class TodoController {

    private final TodoService todoService;
    private final UserService userService;

    // 사용자의 할 일 목록을 조회하여 뷰에 전달
    @GetMapping
    public String listTodos(@AuthenticationPrincipal CustomUser customUser, Model model) {
        // 사용자 정보 DB 조회
        Optional<User> user = userService.findByUsername(customUser.getUsername());
        if (user.isEmpty()) { // 사용자가 존재하지 않으면 로그인 페이지로 리다이렉트
            return "redirect:/login";
        }

        // 해당 사용자에 대한 할일 목록 조회
        List<Todo> todos = todoService.getTodosByUser(user.get());
        model.addAttribute("todos", todos); // 모델이 데이터 추가
        return "todos"; // "todos.html 뷰 반환
    }

    // 새로운 할 일을 추가하는 메서드
    @PostMapping("/add")
    public String addTodo(@AuthenticationPrincipal CustomUser customUser, @ModelAttribute Todo todo) {
        User user = new User(); // 사용자 객체 생성
        user.setId(customUser.getUserId()); // 로그인한 사용자 ID 설정

        // 할 일을 사용자와 함께 저장
        todoService.addTodo(todo, user);
        return "redirect:/todos"; // 목록 페이지로 리다이렉트
    }

    // 특정 ID의 할 일을 삭제하는 메서드
    @PostMapping("/delete/{id}")
    public String deleteTodo(@PathVariable("id") Long id, @AuthenticationPrincipal CustomUser customUser) {
        User user = new User();
        user.setId(customUser.getUserId());

        // 사용자 정보와 함께 해당 ID의 할 일을 삭제
        todoService.deleteTodoById(id, user);
        return "redirect:/todos"; // 목록 페이지로 리다이렉트
    }

    // 특정 ID의 할 일을 수정하기 위해 데이터를 조회하는 메서드
    @GetMapping("/edit/{id}")
    public String editTodo(@PathVariable("id") Long id, Model model, @AuthenticationPrincipal CustomUser customUser) {
        Long userId = customUser.getUserId();

        // 해당 ID의 할 일 조회
        Optional<Todo> todo = todoService.getTodoById(id);

        // 해당 할 일이 존재하고 로그인한 사용자가 소유자인지 확인
        if (todo.isPresent() && todo.get().getUser().getId().equals(userId)) {
            model.addAttribute("todo", todo.get()); // 모델에 할 일 정보 추가
            return "edit_todo"; // 수정 폼 페이지 반환
        }

        return "redirect:/todos"; //조건 미충족 시 목록으로 리다이렉트
    }

    // 특정 ID의 할 일을 실제로 수정하는 메서드
    @PostMapping("/update/{id}")
    public String updateTodo(
            @PathVariable("id") Long id, // 수정할 할 일 ID
            @RequestParam("title") String title, // 새 제목
            @RequestParam("description") String description, // 새 설명
            @AuthenticationPrincipal CustomUser customUser // 인증정보
    ) {
        User user = new User();
        user.setId(customUser.getUserId());

        // 수정된 정보로 업데이트
        todoService.updateTodo(id, title, description, user);
        return "redirect:/todos"; // 목록으로 리다이렉트
    }
}

/**
 * 보안 및 인증 처리 방식 요약
 * - Authentication 객체에서 로그인된 사용자 정보를 꺼냄
 * - OAuth2 로그인 사용자의 경우 CustomOAuth2User 로 캐스팅
 * - 사용자 ID로 할 일 소유자와 비교하여 권한이 있는 사용자만 조작 가능
 */

/**
 * 연계 흐름 예시
 * 1. 사용자가 로그인(OAuth2) -> CustomOAuthUser 로 인증 정보 저장
 * 2. 사용자가 /todos 접근 -> 해당 사용자에게 연결된 Todo 목록 조회
 * 3. 할 일 추가/삭제/수정 -> 사용자 ID로 검증 후 조작
 */
