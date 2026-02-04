package com.dailyjournal.dto;

import com.dailyjournal.entity.Journal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Journal Response DTO
 * 日记响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JournalResponse {

    private Long id;
    private String title;
    private String content;
    private String mood;
    private String weather;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Convert from entity
    public static JournalResponse fromEntity(Journal journal) {
        return JournalResponse.builder()
                .id(journal.getId())
                .title(journal.getTitle())
                .content(journal.getContent())
                .mood(journal.getMood())
                .weather(journal.getWeather())
                .createdAt(journal.getCreatedAt())
                .updatedAt(journal.getUpdatedAt())
                .build();
    }
}
