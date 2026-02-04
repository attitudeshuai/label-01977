package com.dailyjournal.controller;

import com.dailyjournal.dto.ApiResponse;
import com.dailyjournal.dto.CalendarDayResponse;
import com.dailyjournal.security.CurrentUser;
import com.dailyjournal.security.JwtUserPrincipal;
import com.dailyjournal.service.JournalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Calendar Controller
 * 日历控制器
 */
@RestController
@RequestMapping("/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final JournalService journalService;

    /**
     * GET /calendar/{year}/{month} - 获取某月日历数据
     */
    @GetMapping("/{year}/{month}")
    public ResponseEntity<ApiResponse<List<CalendarDayResponse>>> getCalendarMonth(
            @CurrentUser JwtUserPrincipal principal,
            @PathVariable int year,
            @PathVariable int month) {
        
        if (month < 1 || month > 12) {
            return ResponseEntity.badRequest().body(ApiResponse.error("月份无效"));
        }
        
        List<CalendarDayResponse> data = journalService.getCalendarMonth(
                principal.getUserId(), year, month);
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}
