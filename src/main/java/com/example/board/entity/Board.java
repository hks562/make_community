package com.example.board.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "board")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String content;

    @Column(nullable = false, length = 30)
    private String nickname;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(name = "ip_address", nullable = false, length = 45)
    private String ipAddress;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public Board(String title, String content, String nickname, String password, String ipAddress) {
        this.title = title;
        this.content = content;
        this.nickname = nickname;
        this.password = password;
        this.ipAddress = ipAddress;
    }

    // 게시글 수정을 위한 비즈니스 메서드
    public void update(String content) {
        this.content = content;
    }
}