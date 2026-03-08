package com.example.demo.domain.board.dto;

import com.example.demo.domain.board.entity.BoardEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BoardResponseDto {

    private Long id;
    private String title;
    private String content;
    private String nickname; // 유저 닉네임

    public static BoardResponseDto from(BoardEntity boardEntity) {
        return BoardResponseDto.builder()
                .id(boardEntity.getId())
                .title(boardEntity.getTitle())
                .content(boardEntity.getContent())
                .nickname(boardEntity.getUserEntity().getNickname())
                .build();
    }
}
