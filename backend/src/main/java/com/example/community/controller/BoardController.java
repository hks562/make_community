package com.example.community.controller;

import com.example.community.dto.BoardDeleteRequestDto;
import com.example.community.dto.BoardRequestDto;
import com.example.community.dto.BoardResponseDto;
import com.example.community.dto.BoardUpdateRequestDto;
import com.example.community.service.BoardService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    // 글 등록 API (POST /api/boards)
    @PostMapping
    public ResponseEntity<Long> createPost(@RequestBody BoardRequestDto requestDto, HttpServletRequest request) {
        String clientIp = getClientIp(request);
        Long savedId = boardService.createPost(requestDto, clientIp);
        return ResponseEntity.ok(savedId);
    }

    // 전체 글 목록 조회 API (GET /api/boards)
    @GetMapping
    public ResponseEntity<List<BoardResponseDto>> getAllPosts() {
        List<BoardResponseDto> posts = boardService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    // 단일 글 상세 조회 API (GET /api/boards/{id})
    @GetMapping("/{id}")
    public ResponseEntity<BoardResponseDto> getPost(@PathVariable("id") Long id) {
        BoardResponseDto post = boardService.getPost(id);
        return ResponseEntity.ok(post);
    }

    // 작성자의 실제 IP 추출 (프록시/공유기 환경 고려)
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
    // 글 수정 API PUT /api/boards/{id}
    @PutMapping("/{id}")
    public ResponseEntity<String> updatePost(
            @PathVariable Long id,
            @RequestBody BoardUpdateRequestDto requestDto) {
        boardService.updatePost(id, requestDto);
        return ResponseEntity.ok("게시글이 수정되었습니다.");
    }

    // 글 삭제 API DELETE /api/boards/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(
            @PathVariable Long id,
            @RequestBody BoardDeleteRequestDto requestDto) {
        boardService.deletePost(id, requestDto);
        return ResponseEntity.ok("게시글이 삭제되었습니다.");
    }
}