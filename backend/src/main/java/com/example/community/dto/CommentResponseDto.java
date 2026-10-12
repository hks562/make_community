package com.example.community.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CommentResponseDto {
    private Long id;
    private Long boardId;
    private String nickname;
    private String content;
    private String ipAddress;
    private LocalDateTime createdAt;
}