package com.example.community.mapper;

import com.example.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {
    // 특정 게시글의 댓글 목록 조회
    List<Comment> selectCommentsByBoardId(@Param("boardId") Long boardId);
    // 댓글 등록
    int insertComment(Comment comment);

    // 댓글 단건 비밀번호 조회 (삭제 검증용)
    String selectPasswordById(@Param("id") Long id);

    // 댓글 삭제
    int deleteComment(@Param("id") Long id);
}