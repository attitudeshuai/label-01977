package com.dailyjournal.service;

import com.dailyjournal.dto.AuthRequest;
import com.dailyjournal.dto.AuthResponse;
import com.dailyjournal.entity.User;
import com.dailyjournal.repository.UserRepository;
import com.dailyjournal.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Auth Service
 * 认证服务
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    /**
     * Register new user
     */
    @Transactional
    public AuthResponse.TokenResponse register(AuthRequest.Register request) {
        // Check if username exists
        if (userRepository.existsByUsername(request.getUsername())) {
            return AuthResponse.TokenResponse.builder()
                    .success(false)
                    .message("用户名已存在")
                    .build();
        }

        // Check if email exists
        if (userRepository.existsByEmail(request.getEmail())) {
            return AuthResponse.TokenResponse.builder()
                    .success(false)
                    .message("邮箱已被注册")
                    .build();
        }

        // Create user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        user = userRepository.save(user);

        // Generate token
        String token = tokenProvider.generateToken(user.getId(), user.getUsername());

        return AuthResponse.TokenResponse.builder()
                .success(true)
                .message("注册成功")
                .token(token)
                .user(toUserInfo(user))
                .build();
    }

    /**
     * Login user
     */
    public AuthResponse.TokenResponse login(AuthRequest.Login request) {
        // Find user by username or email
        User user = userRepository.findByUsername(request.getUsername())
                .orElseGet(() -> userRepository.findByEmail(request.getUsername()).orElse(null));

        if (user == null) {
            return AuthResponse.TokenResponse.builder()
                    .success(false)
                    .message("用户不存在")
                    .build();
        }

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return AuthResponse.TokenResponse.builder()
                    .success(false)
                    .message("密码错误")
                    .build();
        }

        // Generate token
        String token = tokenProvider.generateToken(user.getId(), user.getUsername());

        return AuthResponse.TokenResponse.builder()
                .success(true)
                .message("登录成功")
                .token(token)
                .user(toUserInfo(user))
                .build();
    }

    /**
     * Get current user info
     */
    public AuthResponse.UserInfo getCurrentUser(Long userId) {
        return userRepository.findById(userId)
                .map(this::toUserInfo)
                .orElse(null);
    }

    private AuthResponse.UserInfo toUserInfo(User user) {
        return AuthResponse.UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .build();
    }
}
