package com.example.mpa_login.user;

import com.example.mpa_login.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 사용자 이름으로 사용자 정보를 조회하는 메서드
    Optional<User> findByUsername(String username);
}
