package com.example.demo.domain.user.repository;

import com.example.demo.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByUsername(String username);

    // Optional : 데이터가 없어도 null 오류 방지
    Optional<UserEntity> findByUsername(String username);

    void deleteByUsername(String username);
}
