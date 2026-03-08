package com.example.demo.domain.board.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardRequestDto {

    String title;
    String content;
}
