package com.dailyjournal.controller;

import com.dailyjournal.dto.ApiResponse;
import com.dailyjournal.dto.StatsResponse;
import com.dailyjournal.security.CurrentUser;
import com.dailyjournal.security.JwtUserPrincipal;
import com.dailyjournal.service.JournalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Stats Controller
 * 统计控制器
 */
@RestController
@RequestMapping("/stats")
@RequiredArgsConstructor
public class StatsController {

    private final JournalService journalService;

    /**
     * GET /stats/overview - 获取统计概览
     */
    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<StatsResponse>> getOverview(
            @CurrentUser JwtUserPrincipal principal) {
        StatsResponse stats = journalService.getStats(principal.getUserId());
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}
