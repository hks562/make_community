CREATE TABLE comments (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    board_id BIGINT NOT NULL,                    -- 어떤 게시글의 댓글인지 (외래키)
    nickname NVARCHAR(50) NOT NULL,               -- 작성자
    password NVARCHAR(100) NOT NULL,              -- 삭제/수정용 비밀번호
    content NVARCHAR(1000) NOT NULL,              -- 댓글 내용
    ip_address VARCHAR(50),                       -- 작성자 IP 주소 (마스킹용)
    created_at DATETIME2 DEFAULT SYSDATETIME(),  -- 작성일시
    CONSTRAINT FK_comments_board FOREIGN KEY (board_id) 
        REFERENCES boards(id) ON DELETE CASCADE   -- 게시글 삭제 시 관련 댓글도 자동 삭제
);