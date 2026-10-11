package com.example.board.service;

import com.example.board.dto.BoardRequestDto;
import com.example.board.dto.BoardResponseDto;
import com.example.board.entity.Board;
import com.example.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    // 1. 글 등록
    @Transactional
    public Long createPost(BoardRequestDto requestDto, String clientIp) {
        Board board = Board.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .nickname(requestDto.getNickname())
                .password(requestDto.getPassword())
                .ipAddress(clientIp)
                .build();

        return boardRepository.save(board).getId();
    }

    // 2. 전체 글 목록 조회 (최신순)
    @Transactional(readOnly = true)
    public List<BoardResponseDto> getAllPosts() {
        return boardRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(BoardResponseDto::new)
                .collect(Collectors.toList());
    }

    // 3. 글 상세 조회
    @Transactional(readOnly = true)
    public BoardResponseDto getPost(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다. id=" + id));
        return new BoardResponseDto(board);
    }
}