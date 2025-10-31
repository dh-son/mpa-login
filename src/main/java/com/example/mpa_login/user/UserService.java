package com.example.mpa_login.user;

import com.example.mpa_login.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // 비밀번호 암호화를 위한 인코더

    // 회원가입: 새 사용자를 등록하는 메서드
    public User registerUser(String username, String password) {
        // 이미 동일한 username이 존재하는지 확인
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        // 새로운 User 객체 생성 및 정보 설정
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));

        return userRepository.save(user);
    }

    // 사용자 이름으로 사용자 정보를 조회하는 메서드
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
