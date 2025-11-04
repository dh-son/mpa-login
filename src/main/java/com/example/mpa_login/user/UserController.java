package com.example.mpa_login.user;

import com.example.mpa_login.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.regex.Pattern;

@RequiredArgsConstructor
@Controller
@RequestMapping("/users") // /users 경로로 들어오는 요청들을 처리함
public class UserController {

    private final UserService userService;

    // 회원가입 폼을 보여주는 메서드
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        // 빈 User 객체를 모델이 추가하여 폼에서 바인딩할 수 있게함
        model.addAttribute("user", new User());
        return "register"; // "register.html 뷰 반환
    }

    // 회원 가입 처리를 담당하는 메서드
    @PostMapping("/register")
    public String register(
            @ModelAttribute("user") User user, // 폼에서 전달된 사용자 정보 바인딩
            BindingResult result, // 유효성 검사 결과를 담는 객체
            Model model // 뷰로 데이터를 전달하기 위한 객체
    ) {
        // 이메일 형식을 검증하기 위한 정규 표현식
        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        // 사용자 이름(이메일)이 정규식과 일치하지 않으면 에러 처리
        if (!Pattern.matches(emailPattern, user.getUsername())) {
            // bindingResult에 에러 등록
            result.rejectValue("username", "error.user", "Invalid email format. Please enter a valid email address.");
            // 뷰에 표시할 에러 메시지 추가
            model.addAttribute("emailError", "Invalid email format. Please enter a valid email address.");
        }

        // 에러가 존재하면 다시 회원가입 페이지로 이동
        if (result.hasErrors()) {
            return "register"; // register.html을 다시 렌더링
        }

        // 이미 존재하는 사용자명(이메일)이 있는지 확인
        if (userService.findByUsername(user.getUsername()).isPresent()) {
            // 중복 사용자 에러 메시지 추가
            model.addAttribute("error", "Username already exists");
            return "register"; // 다시 회원 가입 페이지로 이동
        }

        // 사용자 등록 처리
        userService.registerUser(user.getUsername(), user.getPassword());
        return "redirect:/login"; // 로그인 페이지로 리다이렉트
    }
}
