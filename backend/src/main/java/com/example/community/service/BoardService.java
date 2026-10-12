package com.example.community.service;

import com.example.community.dto.BoardDeleteRequestDto;
import com.example.community.dto.BoardRequestDto;
import com.example.community.dto.BoardResponseDto;
import com.example.community.dto.BoardUpdateRequestDto;
import com.example.community.entity.Board;
import com.example.community.mapper.BoardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardMapper boardMapper;

    //글 쓰기
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
    //글 존재 여부
    private Board findBoardById(Long id) {
        return boardMapper.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다. id=" + id));
    }

    //글 조회(전체)
    public List<BoardResponseDto> getAllPosts() {
        return boardMapper.findAll().stream()
                .map(BoardResponseDto::new)
                .toList();
    }

    //글 조회(상세)
    public BoardResponseDto getPost(Long id) {
        Board board = findBoardById(id);
        return new BoardResponseDto(board);
    }
    // 글 수정
    public Long updatePost(Long id, BoardUpdateRequestDto requestDto) {
        Board board = findBoardById(id);

        // 비밀번호 검증
        if (!board.getPassword().equals(requestDto.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        board.setContent(requestDto.getContent());
        boardMapper.update(board);
        return id;
    }
    // 글 삭제
    public void deletePost(Long id, BoardDeleteRequestDto requestDto) {
        Board board = findBoardById(id);

        if (!board.getPassword().equals(requestDto.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        boardMapper.delete(id);
    }
}