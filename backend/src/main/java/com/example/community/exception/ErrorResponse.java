package com.example.community.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ErrorResponse {
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final int status;       // HTTP 상태 코드 (예: 404, 400, 500)
    private final String error;     // 에러 이름 (예: NOT_FOUND, BAD_REQUEST)
    private final String code;      // 서버 자체 정의 코드 (예: BOARD_NOT_FOUND)
    private final String message;   // 사용자에게 보여줄 친절한 설명
}