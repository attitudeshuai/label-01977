package com.dailyjournal.security;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * JWT User Principal
 * JWT 用户主体 - 存储在 SecurityContext 中
 */
@Data
@AllArgsConstructor
public class JwtUserPrincipal {
    private Long userId;
    private String username;
}
