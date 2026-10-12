package com.example.board.mapper;

import com.example.board.entity.Board;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface BoardMapper {
    void save(Board board);
    List<Board> findAll();
    Optional<Board> findById(Long id);

    int update(Board board);
    int delete(Long id);
}