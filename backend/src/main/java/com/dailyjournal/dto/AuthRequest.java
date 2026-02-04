package com.dailyjournal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Auth Request DTOs
 * 认证请求 DTO
 */
public class AuthRequest {

    @Data
    public static class Register {
        @NotBlank(message = "用户名不能为空")
        @Size(min = 2, max = 50, message = "用户名长度应在2-50字符之间")
        private String username;

        @NotBlank(message = "邮箱不能为空")
        @Email(message = "邮箱格式不正确")
        private String email;

        @NotBlank(message = "密码不能为空")
        @Size(min = 6, max = 100, message = "密码长度应在6-100字符之间")
        private String password;
    }

    @Data
    public static class Login {
        @NotBlank(message = "用户名/邮箱不能为空")
        private String username; // can be username or email

        @NotBlank(message = "密码不能为空")
        private String password;
    }
}
