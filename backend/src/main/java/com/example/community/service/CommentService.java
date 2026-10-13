package com.example.community.service;

import com.example.community.dto.CommentRequestDto;
import com.example.community.dto.CommentResponseDto;
import com.example.community.entity.Comment;
import com.example.community.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentMapper commentMapper;

    // [댓글 등록 메서드]
    @Transactional
    public void createComment(Long boardId, CommentRequestDto dto, String ipAddress) {
        Comment comment = Comment.builder()
                .boardId(boardId)
                .nickname(dto.getNickname())
                .password(dto.getPassword())
                .content(dto.getContent())
                .ipAddress(ipAddress)
                .build();

        commentMapper.insertComment(comment);
    }

    // [댓글 조회 메서드]
    @Transactional(readOnly = true)
    public List<CommentResponseDto> getComments(Long boardId) {
        List<Comment> comments = commentMapper.selectCommentsByBoardId(boardId);

        return comments.stream()
                .map(c -> new CommentResponseDto(
                        c.getId(),
                        c.getNickname(),
                        c.getContent(),
                        c.getCreatedAt(),
                        c.getIpAddress()
                ))
                .collect(Collectors.toList());
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