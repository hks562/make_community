package com.example.community.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentRequestDto {
    private Long id;
    private String nickname;
    private String password;
    private String content;
    private String ipAddress; // 컨트롤러에서 채워줄 IP
}