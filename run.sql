USE board_db;
--DELETE FROM board WHERE id = 2;
INSERT INTO board (title, content, nickname, password, ip_address)
VALUES 
(N'두 번째 익명 글', N'스프링 부트 연동 확인용 내용입니다.', N'지나가던사람', N'1234', '192.168.0.15'),
(N'세 번째 공지 느낌 글', N'익명 게시판 테스트 중입니다.', N'관리자', N'1234', '112.158.42.11');
select * from board ORDER BY created_at DESC;
