package com.example.community.service;

import com.example.community.dto.UserLoginRequestDto;
import com.example.community.dto.UserResponseDto;
import com.example.community.dto.UserSignupRequestDto;
import com.example.community.entity.User;
import com.example.community.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponseDto signup(UserSignupRequestDto dto) {
        if (userMapper.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword())); // BCrypt 암호화
        user.setNickname(dto.getNickname());

        userMapper.insertUser(user);
        return new UserResponseDto(user.getId(), user.getUsername(), user.getNickname());
    }

    public UserResponseDto login(UserLoginRequestDto dto) {
        User user = userMapper.findByUsername(dto.getUsername());
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        return new UserResponseDto(user.getId(), user.getUsername(), user.getNickname());
    }
}