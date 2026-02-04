package com.dailyjournal.service;

import com.dailyjournal.dto.*;
import com.dailyjournal.entity.Journal;
import com.dailyjournal.entity.User;
import com.dailyjournal.repository.JournalRepository;
import com.dailyjournal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Journal Service
 * 日记服务
 */
@Service
@RequiredArgsConstructor
public class JournalService {

    private final JournalRepository journalRepository;
    private final UserRepository userRepository;

    /**
     * Get paginated journals for user
     */
    public PageResponse<JournalResponse> getJournals(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Journal> journalPage = journalRepository.findByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(userId, pageable);
        
        Page<JournalResponse> responsePage = journalPage.map(JournalResponse::fromEntity);
        return PageResponse.from(responsePage);
    }

    /**
     * Get single journal by id
     */
    public JournalResponse getJournal(Long journalId, Long userId) {
        return journalRepository.findByIdAndUserIdAndDeletedAtIsNull(journalId, userId)
                .map(JournalResponse::fromEntity)
                .orElse(null);
    }

    /**
     * Create new journal
     */
    @Transactional
    public JournalResponse createJournal(JournalRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

        Journal journal = Journal.builder()
                .user(user)
                .title(request.getTitle())
                .content(request.getContent())
                .mood(request.getMood())
                .weather(request.getWeather())
                .build();

        journal = journalRepository.save(journal);
        return JournalResponse.fromEntity(journal);
    }

    /**
     * Update existing journal
     */
    @Transactional
    public JournalResponse updateJournal(Long journalId, JournalRequest request, Long userId) {
        Journal journal = journalRepository.findByIdAndUserIdAndDeletedAtIsNull(journalId, userId)
                .orElseThrow(() -> new IllegalArgumentException("日记不存在"));

        journal.setTitle(request.getTitle());
        journal.setContent(request.getContent());
        journal.setMood(request.getMood());
        journal.setWeather(request.getWeather());

        journal = journalRepository.save(journal);
        return JournalResponse.fromEntity(journal);
    }

    /**
     * Soft delete journal
     */
    @Transactional
    public boolean deleteJournal(Long journalId, Long userId) {
        Journal journal = journalRepository.findByIdAndUserIdAndDeletedAtIsNull(journalId, userId)
                .orElse(null);

        if (journal == null) {
            return false;
        }

        journal.setDeletedAt(LocalDateTime.now());
        journalRepository.save(journal);
        return true;
    }

    /**
     * Search journals by keyword
     */
    public PageResponse<JournalResponse> searchJournals(Long userId, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Journal> journalPage = journalRepository.searchByKeyword(userId, keyword, pageable);
        
        Page<JournalResponse> responsePage = journalPage.map(JournalResponse::fromEntity);
        return PageResponse.from(responsePage);
    }

    /**
     * Get calendar data for a month
     */
    public List<CalendarDayResponse> getCalendarMonth(Long userId, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startDate = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endDate = yearMonth.plusMonths(1).atDay(1).atStartOfDay();

        List<Journal> journals = journalRepository.findByUserIdAndMonth(userId, startDate, endDate);

        return journals.stream()
                .map(j -> CalendarDayResponse.builder()
                        .date(j.getCreatedAt().toLocalDate())
                        .journalId(j.getId())
                        .mood(j.getMood())
                        .weather(j.getWeather())
                        .title(j.getTitle())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Get stats overview
     */
    public StatsResponse getStats(Long userId) {
        long total = journalRepository.countByUserIdAndDeletedAtIsNull(userId);

        // This month count
        YearMonth currentMonth = YearMonth.now();
        LocalDateTime startOfMonth = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime startOfNextMonth = currentMonth.plusMonths(1).atDay(1).atStartOfDay();
        long thisMonth = journalRepository.countByUserIdAndMonth(userId, startOfMonth, startOfNextMonth);

        // Top mood
        String topMood = null;
        long topMoodCount = 0;
        List<Object[]> moodStats = journalRepository.findMostFrequentMood(userId);
        if (!moodStats.isEmpty()) {
            Object[] top = moodStats.get(0);
            topMood = (String) top[0];
            topMoodCount = (Long) top[1];
        }

        return StatsResponse.builder()
                .totalJournals(total)
                .thisMonthJournals(thisMonth)
                .topMood(topMood)
                .topMoodCount(topMoodCount)
                .build();
    }
}
