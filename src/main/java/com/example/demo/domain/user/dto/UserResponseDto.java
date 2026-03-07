package com.example.demo.domain.user.dto;

import com.example.demo.domain.user.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
// 응답에 필요한 필드만 명시했으므로 클래스 자체에 Builder
@Builder
public class UserResponseDto {

    private Long id;
    private String username;
    private String nickname;
    private String role;

    public static UserResponseDto from(UserEntity userEntity) {
        return UserResponseDto.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .nickname(userEntity.getNickname())
                .role(userEntity.getRole().toString())
                .build();
    }
}
