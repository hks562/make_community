package com.example.community.controller;

import com.example.community.dto.CommentDeleteRequestDto;
import com.example.community.dto.CommentRequestDto;
import com.example.community.dto.CommentResponseDto;
import com.example.community.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000") // Nuxt 도메인 허용
public class CommentController {

    private final CommentService commentService;

    // 특정 게시글의 댓글 목록 조회
    @GetMapping("/api/boards/{boardId}/comments")
    public ResponseEntity<List<CommentResponseDto>> getComments(@PathVariable Long boardId) {
        return ResponseEntity.ok(commentService.getComments(boardId));
    }

    // 댓글 작성
    @PostMapping("/api/boards/{boardId}/comments")
    public ResponseEntity<Void> createComment(
            @PathVariable Long boardId,
            @RequestBody CommentRequestDto dto,
            HttpServletRequest request
    ) {
        // IP 추출 (프록시/로컬 환경 대응)
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getRemoteAddr();
        } else {
            clientIp = clientIp.split(",")[0].trim();
        }
        dto.setIpAddress(clientIp);

        commentService.createComment(boardId, dto, clientIp);
        return ResponseEntity.ok().build();
    }

    // 댓글 삭제
    @DeleteMapping("/api/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @RequestBody CommentDeleteRequestDto dto
    ) {
        commentService.deleteComment(commentId, dto.getPassword());
        return ResponseEntity.ok().build();
    }
}