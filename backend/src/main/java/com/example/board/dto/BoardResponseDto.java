package com.example.board.dto;

import com.example.board.entity.Board;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BoardResponseDto {
    private Long id;
    private String title;
    private String content;
    private String nickname;
    private String ipAddress;
    private LocalDateTime createdAt;

    public BoardResponseDto(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.nickname = board.getNickname();
        this.ipAddress = board.getIpAddress();
        this.createdAt = board.getCreatedAt();
    }
}