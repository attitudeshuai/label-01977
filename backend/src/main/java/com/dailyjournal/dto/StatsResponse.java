package com.dailyjournal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Stats Response DTO
 * 统计响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatsResponse {

    private long totalJournals;
    private long thisMonthJournals;
    private String topMood;
    private long topMoodCount;
}
