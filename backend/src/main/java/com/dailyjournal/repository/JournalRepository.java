package com.dailyjournal.repository;

import com.dailyjournal.entity.Journal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Journal Repository
 * 日记数据访问层
 */
@Repository
public interface JournalRepository extends JpaRepository<Journal, Long> {

    // Find all journals by user (not deleted) with pagination
    Page<Journal> findByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // Find journal by id and user (not deleted)
    Optional<Journal> findByIdAndUserIdAndDeletedAtIsNull(Long id, Long userId);

    // Search journals by title or content
    @Query("SELECT j FROM Journal j WHERE j.user.id = :userId AND j.deletedAt IS NULL " +
           "AND (LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(j.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY j.createdAt DESC")
    Page<Journal> searchByKeyword(@Param("userId") Long userId, @Param("keyword") String keyword, Pageable pageable);

    // Get journals for a specific month (for calendar view)
    @Query("SELECT j FROM Journal j WHERE j.user.id = :userId AND j.deletedAt IS NULL " +
           "AND j.createdAt >= :startDate AND j.createdAt < :endDate " +
           "ORDER BY j.createdAt ASC")
    List<Journal> findByUserIdAndMonth(@Param("userId") Long userId,
                                       @Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);

    // Count total journals by user
    long countByUserIdAndDeletedAtIsNull(Long userId);

    // Count journals in current month
    @Query("SELECT COUNT(j) FROM Journal j WHERE j.user.id = :userId AND j.deletedAt IS NULL " +
           "AND j.createdAt >= :startDate AND j.createdAt < :endDate")
    long countByUserIdAndMonth(@Param("userId") Long userId,
                               @Param("startDate") LocalDateTime startDate,
                               @Param("endDate") LocalDateTime endDate);

    // Get most frequent mood
    @Query("SELECT j.mood, COUNT(j.mood) as cnt FROM Journal j " +
           "WHERE j.user.id = :userId AND j.deletedAt IS NULL AND j.mood IS NOT NULL " +
           "GROUP BY j.mood ORDER BY cnt DESC")
    List<Object[]> findMostFrequentMood(@Param("userId") Long userId);
}
