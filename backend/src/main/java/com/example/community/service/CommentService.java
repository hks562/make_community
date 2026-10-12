package com.example.community.service;

import com.example.community.dto.CommentRequestDto;
import com.example.community.dto.CommentResponseDto;
import com.example.community.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentMapper commentMapper;

    // 댓글 목록 조회
    @Transactional(readOnly = true)
    public List<CommentResponseDto> getComments(Long boardId) {
        return commentMapper.selectCommentsByBoardId(boardId);
    }

    // 댓글 등록
    @Transactional
    public void createComment(Long boardId, CommentRequestDto dto) {
        commentMapper.insertComment(boardId, dto);
    }

    // 댓글 삭제 (비밀번호 검증)
    @Transactional
    public void deleteComment(Long commentId, String password) {
        String dbPassword = commentMapper.selectPasswordById(commentId);
        if (dbPassword == null) {
            throw new IllegalArgumentException("존재하지 않는 댓글입니다.");
        }
        if (!dbPassword.equals(password)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        commentMapper.deleteComment(commentId);
    }
}