package com.example.demo.domain.board.service;

import com.example.demo.domain.board.dto.BoardRequestDto;
import com.example.demo.domain.board.dto.BoardResponseDto;
import com.example.demo.domain.board.entity.BoardEntity;
import com.example.demo.domain.board.repository.BoardRepository;
import com.example.demo.domain.user.entity.UserEntity;
import com.example.demo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    // 유저 접근 권한 체크
    public Boolean isAccess(Long id) {

        // 현재 로그인 되어 있는 username
        String sessionUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        // iterator().next()는 권한이 여러 개일 때 첫 번째만 가져오므로 아래처럼 전체 권한 확인하는 방식이 더 안전함
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        // ADMIN일 경우 모두 접근 가능
        if(isAdmin) return true;

        // 특정 게시글이 본인의 글인지 확인
        String boardUsername = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다."))
                .getUserEntity().getUsername();
        if(sessionUsername.equals(boardUsername)) return true;

        // 그 외 모두 불가
        return false;
    }

    // 게시글 생성
    @Transactional
    public void createBoard(BoardRequestDto boardRequestDto) {

        // 해당 게시글 작성 유저 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // 로그인 유저의 username으로 DB 조회하여 userEntity 획득
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));

        // 전달 받은 dto와 로그인한 유저의 entity로 board entity builder
        BoardEntity boardEntity = BoardEntity.builder()
                .title(boardRequestDto.getTitle())
                .content(boardRequestDto.getContent())
                .userEntity(userEntity)
                .build();

        // db에 저장
        boardRepository.save(boardEntity);
    }

    // 게시글 id로 단 건 조회
    public BoardResponseDto getBoard(Long id) {

        // id로 게시글 조회
        BoardEntity boardEntity = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다."));

        // boardEntity로 responseDto builder 리턴
        return BoardResponseDto.from(boardEntity);
    }

    // 모든 게시글 조회
    public List<BoardResponseDto> getBoards() {

        // db에서 모든 게시글 조회해서 entity 매핑
        List<BoardEntity> boardList = boardRepository.findAll();

        // 엔티티 1개씩 꺼내서 List에 담기
        List<BoardResponseDto> boardDtos = new ArrayList<>();
        for(BoardEntity board : boardList) {
            boardDtos.add(BoardResponseDto.from(board));
        }

        return boardDtos;
    }

    // 게시글 수정
    @Transactional
    public void updateBoard(Long id, BoardRequestDto boardRequestDto) {

        // entity 조회
        BoardEntity boardEntity = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다."));

        // entity 메서드로 수정
        if(boardEntity.getTitle() != null && !boardEntity.getTitle().isEmpty()) {
            boardEntity.changeTitle(boardRequestDto.getTitle());
        }

        if(boardEntity.getContent() != null && !boardEntity.getContent().isEmpty()) {
            boardEntity.changeContent(boardRequestDto.getContent());
        }

        boardRepository.save(boardEntity);
    }

    // 게시글 삭제
    @Transactional
    public void deleteBoard(Long id) {
        boardRepository.deleteById(id);
    }
}
