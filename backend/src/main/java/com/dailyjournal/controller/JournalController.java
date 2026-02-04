package com.dailyjournal.controller;

import com.dailyjournal.dto.*;
import com.dailyjournal.security.CurrentUser;
import com.dailyjournal.security.JwtUserPrincipal;
import com.dailyjournal.service.JournalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Journal Controller
 * 日记控制器
 */
@RestController
@RequestMapping("/journals")
@RequiredArgsConstructor
public class JournalController {

    private final JournalService journalService;

    /**
     * GET /journals - 获取日记列表 (分页)
     */
    @GetMapping
    public ResponseEntity<PageResponse<JournalResponse>> getJournals(
            @CurrentUser JwtUserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<JournalResponse> response = journalService.getJournals(principal.getUserId(), page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /journals/{id} - 获取日记详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JournalResponse>> getJournal(
            @CurrentUser JwtUserPrincipal principal,
            @PathVariable Long id) {
        JournalResponse journal = journalService.getJournal(id, principal.getUserId());
        if (journal == null) {
            return ResponseEntity.status(404).body(ApiResponse.error("日记不存在"));
        }
        return ResponseEntity.ok(ApiResponse.success(journal));
    }

    /**
     * POST /journals - 创建日记
     */
    @PostMapping
    public ResponseEntity<ApiResponse<JournalResponse>> createJournal(
            @CurrentUser JwtUserPrincipal principal,
            @Valid @RequestBody JournalRequest request) {
        JournalResponse journal = journalService.createJournal(request, principal.getUserId());
        return ResponseEntity.ok(ApiResponse.success("创建成功", journal));
    }

    /**
     * PUT /journals/{id} - 更新日记
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<JournalResponse>> updateJournal(
            @CurrentUser JwtUserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody JournalRequest request) {
        try {
            JournalResponse journal = journalService.updateJournal(id, request, principal.getUserId());
            return ResponseEntity.ok(ApiResponse.success("更新成功", journal));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * DELETE /journals/{id} - 删除日记
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteJournal(
            @CurrentUser JwtUserPrincipal principal,
            @PathVariable Long id) {
        boolean deleted = journalService.deleteJournal(id, principal.getUserId());
        if (!deleted) {
            return ResponseEntity.status(404).body(ApiResponse.error("日记不存在"));
        }
        return ResponseEntity.ok(ApiResponse.success("删除成功", null));
    }

    /**
     * GET /journals/search - 搜索日记
     */
    @GetMapping("/search")
    public ResponseEntity<PageResponse<JournalResponse>> searchJournals(
            @CurrentUser JwtUserPrincipal principal,
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<JournalResponse> response = journalService.searchJournals(
                principal.getUserId(), q, page, size);
        return ResponseEntity.ok(response);
    }
}
