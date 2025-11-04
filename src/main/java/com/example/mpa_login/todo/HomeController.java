package com.example.mpa_login.todo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // 루트 경로("/")에 대한 GET 요청을 처리하는 메서드
    @GetMapping("/")
    public String home() {
        return "index"; // index 라는 이름의 뷰(템플릿)를 반환
    }

    // "/login" 경로에 대한 GET 요청을 처리하는 메서드
    @GetMapping("/login")
    public String login() {
        return "login"; // login 이라는 이름의 뷰(템플릿)을 반환
    }

    // "logout" 경로에 대한 GET 요청을 처리하는 메서드
    @GetMapping("/logout")
    public String logout() {
        return "redirect:/login?logout"; // 로그아웃 후 /login?logout 경로로 리다이렉트
    }
}
