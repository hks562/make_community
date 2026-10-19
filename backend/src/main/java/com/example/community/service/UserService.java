package com.example.community.service;

import com.example.community.dto.UserLoginRequestDto;
import com.example.community.dto.UserResponseDto;
import com.example.community.dto.UserSignupRequestDto;
import com.example.community.entity.User;
import com.example.community.exception.CustomException;
import com.example.community.exception.ErrorCode;
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
            throw new CustomException(ErrorCode.DUPLICATE_USERNAME);
        }
        if (userMapper.existsByNickname(dto.getNickname())) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword())); // Bcrypt 암호화
        user.setNickname(dto.getNickname());

        userMapper.insertUser(user);
        return new UserResponseDto(user.getId(), user.getUsername(), user.getNickname());
    }

    public UserResponseDto login(UserLoginRequestDto dto) {
        User user = userMapper.findByUsername(dto.getUsername());
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        return new UserResponseDto(user.getId(), user.getUsername(), user.getNickname());
    }
}