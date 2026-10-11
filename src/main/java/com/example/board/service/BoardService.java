package com.example.board.service;

import com.example.board.dto.BoardRequestDto;
import com.example.board.dto.BoardResponseDto;
import com.example.board.entity.Board;
import com.example.board.mapper.BoardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardMapper boardMapper;

    public Long createPost(BoardRequestDto requestDto, String clientIp) {
        Board board = Board.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .nickname(requestDto.getNickname())
                .password(requestDto.getPassword())
                .ipAddress(clientIp)
                .build();

        boardMapper.save(board);
        return board.getId();
    }

    public List<BoardResponseDto> getAllPosts() {
        return boardMapper.findAll().stream()
                .map(BoardResponseDto::new)
                .toList();
    }

    public BoardResponseDto getPost(Long id) {
        Board board = boardMapper.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다. id=" + id));
        return new BoardResponseDto(board);
    }
}