package com.example.demo.domain.user.entity;

import lombok.Getter;

@Getter
public enum UserRoleType {

    // enum의 객체(인스턴스)
    // "관리자", "유저" 등은 각 enum 인스턴스를 만들 때 전달되는 값
    ADMIN("관리자"),
    USER("유저");

    // enum 클래스의 필드
    private final String description;

    UserRoleType(String description) {
        this.description = description;
    }
}
