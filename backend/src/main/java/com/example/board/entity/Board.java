package com.example.board.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Board {
    private Long id;
    private String title;
    private String content;
    private String nickname;
    private String password;
    private String ipAddress;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 게시글 수정을 위한 비즈니스 메서드
    public void update(String content) {
        this.content = content;
    }
}