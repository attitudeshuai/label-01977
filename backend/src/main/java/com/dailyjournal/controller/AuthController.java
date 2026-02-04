package com.dailyjournal.controller;

import com.dailyjournal.dto.ApiResponse;
import com.dailyjournal.dto.AuthRequest;
import com.dailyjournal.dto.AuthResponse;
import com.dailyjournal.security.CurrentUser;
import com.dailyjournal.security.JwtUserPrincipal;
import com.dailyjournal.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Auth Controller
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /auth/register - 用户注册
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse.TokenResponse> register(
            @Valid @RequestBody AuthRequest.Register request) {
        AuthResponse.TokenResponse response = authService.register(request);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * POST /auth/login - 用户登录
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse.TokenResponse> login(
            @Valid @RequestBody AuthRequest.Login request) {
        AuthResponse.TokenResponse response = authService.login(request);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * GET /auth/me - 获取当前用户信息
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthResponse.UserInfo>> getCurrentUser(
            @CurrentUser JwtUserPrincipal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("未登录"));
        }
        AuthResponse.UserInfo userInfo = authService.getCurrentUser(principal.getUserId());
        if (userInfo == null) {
            return ResponseEntity.status(404).body(ApiResponse.error("用户不存在"));
        }
        return ResponseEntity.ok(ApiResponse.success(userInfo));
    }

    /**
     * POST /auth/logout - 退出登录 (客户端清除 token 即可)
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        return ResponseEntity.ok(ApiResponse.success("退出成功", null));
    }
}
