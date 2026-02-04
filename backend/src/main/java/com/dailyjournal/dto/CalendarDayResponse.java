package com.dailyjournal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Calendar Day Response DTO
 * 日历日期响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarDayResponse {

    private LocalDate date;
    private Long journalId;
    private String mood;
    private String weather;
    private String title;
}
