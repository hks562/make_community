package com.example.community.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
    private Long id;
    private Long boardId;
    private Long userId;
    private String nickname;
    private String password;
    private String content;
    private String ipAddress;
    private LocalDateTime createdAt;
}