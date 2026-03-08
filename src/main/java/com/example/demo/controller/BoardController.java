package com.example.demo.controller;

import com.example.demo.domain.board.dto.BoardRequestDto;
import com.example.demo.domain.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
@Controller
public class BoardController {

    private final BoardService boardService;

    // 글 작성 페이지
    @GetMapping("/board/create")
    public String createPage() {

        return "createBoard";
    }

    // 글 작성 진행
    @PostMapping("/board/create")
    public String createProcess(BoardRequestDto boardRequestDto) {
        boardService.createBoard(boardRequestDto);

        return "redirect:/board/read";
    }

    // 글 목록 페이지
    @GetMapping("/board/read")
    public String readPage(Model model) {

        model.addAttribute("BOARDLIST", boardService.getBoards());

        return "readBoard";
    }

    // 글 읽기
    @GetMapping("/board/read/{id}")
    public String readIdPage(@PathVariable("id") Long id, Model model) {

        model.addAttribute("BOARD", boardService.getBoard(id));

        return "readIdBoard";
    }

    // 글 수정 페이지
    @GetMapping("/board/update/{id}")
    public String updatePage(@PathVariable("id") Long id, Model model) {

        // 접근 권한 확인
        if(!boardService.isAccess(id)) {
            return "redirect:/board/read";
        }

        model.addAttribute("BOARD", boardService.getBoard(id));

        return "updateBoard";
    }

    // 글 수정 진행
    @PostMapping("/board/update/{id}")
    public String updateProcess(@PathVariable("id") Long id, BoardRequestDto boardRequestDto) {

        // 접근 권한 확인
        if(!boardService.isAccess(id)) {
            return "redirect:/board/read";
        }

        boardService.updateBoard(id, boardRequestDto);

        return "redirect:/board/read/" + id;
    }

    // 글 삭제
    @PostMapping("/board/delete/{id}")
    public String deleteBoard(@PathVariable("id") Long id) {

        // 접근 권한 확인
        if(boardService.isAccess(id)) {
            boardService.deleteBoard(id);
        }

        return "redirect:/board/read";
    }
}
