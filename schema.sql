CREATE DATABASE board_db;
GO
USE board_db;
GO

--게시글 db 구조
CREATE TABLE board (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,        --(소규모 익명 게시판) 1씩 id 증가
    title NVARCHAR(200) NOT NULL,
    content NVARCHAR(MAX) NOT NULL,
    nickname NVARCHAR(30) NOT NULL,
    password VARCHAR(100) NOT NULL,             -- 익명 글 수정/삭제용 비번
    ip_address VARCHAR(45) NOT NULL,            -- 작성자 IP (예: 121.135.xxx.xxx)
    created_at DATETIME2 DEFAULT SYSDATETIME(),
    updated_at DATETIME2 DEFAULT SYSDATETIME()
);
GO

--인덱스
-- 1. 전체 최신글 목록용
CREATE INDEX idx_board_created_at ON board (created_at DESC);

-- 2. "특정 닉네임이 쓴 최신글" 검색용
CREATE INDEX idx_board_nickname_created_at ON board (nickname, created_at DESC);

-- 3. "특정 IP가 쓴 최신글" 검색용
CREATE INDEX idx_board_ip_created_at ON board (ip_address, created_at DESC);