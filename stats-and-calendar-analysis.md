# Stats 与 Calendar 模块设计分析

---

## 一、StatsController — 统计概览

### 1.1 入口与调用链

- 接口：`GET /stats/overview`
- Controller：`StatsController.getOverview()`
- Service：`JournalService.getStats(Long userId)`
- 数据来源：`JournalRepository`（Spring Data JPA），底层实体为 `Journal`

`StatsController` 本身非常薄，只做了一件事——从 `@CurrentUser` 注入的 `JwtUserPrincipal` 中取出 `userId`，然后委托给 `JournalService.getStats()`。项目中**没有独立的 StatsService**，统计逻辑全部写在 `JournalService` 里。

### 1.2 三个统计指标的计算逻辑

`JournalService.getStats()` 方法（`JournalService.java:141`）依次计算了三项数据：

#### 指标一：`totalJournals` — 日记总数

```java
long total = journalRepository.countByUserIdAndDeletedAtIsNull(userId);
```

- 调用 `JournalRepository.countByUserIdAndDeletedAtIsNull()`
- 这是 Spring Data JPA 的派生查询方法（Derived Query Method），框架根据方法名自动生成 SQL，等价于：
  ```sql
  SELECT COUNT(*) FROM journals j WHERE j.user_id = ? AND j.deleted_at IS NULL
  ```
- **只统计未软删除的日记**（`deletedAt IS NULL`）

#### 指标二：`thisMonthJournals` — 本月日记数

```java
YearMonth currentMonth = YearMonth.now();
LocalDateTime startOfMonth = currentMonth.atDay(1).atStartOfDay();
LocalDateTime startOfNextMonth = currentMonth.plusMonths(1).atDay(1).atStartOfDay();
long thisMonth = journalRepository.countByUserIdAndMonth(userId, startOfMonth, startOfNextMonth);
```

- 用 `YearMonth.now()` 拿到当前年月，算出本月第一天 00:00:00 和下月第一天 00:00:00 两个时间边界
- 调用 `JournalRepository.countByUserIdAndMonth()`，这是一个自定义 `@Query`：
  ```sql
  SELECT COUNT(j) FROM Journal j
  WHERE j.user.id = :userId AND j.deletedAt IS NULL
  AND j.createdAt >= :startDate AND j.createdAt < :endDate
  ```
- 时间范围是**左闭右开** `[startDate, endDate)`，这是处理时间区间最安全的写法，避免边界秒丢失
- 同样排除了软删除的日记

#### 指标三：`topMood` / `topMoodCount` — 最频繁心情

```java
List<Object[]> moodStats = journalRepository.findMostFrequentMood(userId);
if (!moodStats.isEmpty()) {
    Object[] top = moodStats.get(0);
    topMood = (String) top[0];
    topMoodCount = (Long) top[1];
}
```

- 调用 `JournalRepository.findMostFrequentMood()`，对应的 JPQL：
  ```sql
  SELECT j.mood, COUNT(j.mood) as cnt FROM Journal j
  WHERE j.user.id = :userId AND j.deletedAt IS NULL AND j.mood IS NOT NULL
  GROUP BY j.mood ORDER BY cnt DESC
  ```
- 该查询按 `mood` 分组，计算每种心情出现次数，按次数降序排列
- 返回类型是 `List<Object[]>`，每个 `Object[]` 包含两个元素：`[mood字符串, 计数]`
- **只取第一行**（`moodStats.get(0)`），即出现次数最多的心情
- 注意过滤条件 `j.mood IS NOT NULL`，避免日记没有填心情时干扰统计
- 如果用户没有写过任何带心情的日记，`moodStats` 为空列表，`topMood` 和 `topMoodCount` 保持 `null` 和 `0`

### 1.3 返回给前端的数据结构

`StatsResponse` DTO：

| 字段 | 类型 | 含义 |
|---|---|---|
| `totalJournals` | `long` | 用户未删除日记的总数 |
| `thisMonthJournals` | `long` | 本月未删除日记的数量 |
| `topMood` | `String` | 出现次数最多的心情值（如 "happy"），可能为 null |
| `topMoodCount` | `long` | 该心情出现的次数 |

最终通过 `ApiResponse<StatsResponse>` 包装后返回 JSON，结构大致为：

```json
{
  "success": true,
  "data": {
    "totalJournals": 42,
    "thisMonthJournals": 7,
    "topMood": "happy",
    "topMoodCount": 15
  }
}
```

### 1.4 数据来源总结

所有统计数据都来自同一张 `journals` 表，通过 `Journal` 实体访问。核心字段是：
- `user_id` — 过滤当前用户的日记
- `deleted_at` — 软删除标记，所有查询都加上了 `IS NULL` 条件
- `created_at` — 时间字段，用于本月统计和日历查询
- `mood` — 心情字段，取值范围在注释中标注为 `happy, calm, sad, angry, anxious`

---

## 二、CalendarController — 日历视图

### 2.1 入口与调用链

- 接口：`GET /calendar/{year}/{month}`
- Controller：`CalendarController.getCalendarMonth()`
- Service：`JournalService.getCalendarMonth(Long userId, int year, int month)`
- 数据来源：`JournalRepository.findByUserIdAndMonth()`

`CalendarController` 先做参数校验（月份必须在 1–12 之间），然后委托给 `JournalService.getCalendarMonth()`。

### 2.2 日历数据的计算逻辑

`JournalService.getCalendarMonth()` 方法（`JournalService.java:120`）：

```java
YearMonth yearMonth = YearMonth.of(year, month);
LocalDateTime startDate = yearMonth.atDay(1).atStartOfDay();
LocalDateTime endDate = yearMonth.plusMonths(1).atDay(1).atStartOfDay();

List<Journal> journals = journalRepository.findByUserIdAndMonth(userId, startDate, endDate);
```

1. 根据 `year` 和 `month` 构造 `YearMonth`，算出该月第一天 00:00:00 和下月第一天 00:00:00
2. 调用 `JournalRepository.findByUserIdAndMonth()`，对应 JPQL：
   ```sql
   SELECT j FROM Journal j
   WHERE j.user.id = :userId AND j.deletedAt IS NULL
   AND j.createdAt >= :startDate AND j.createdAt < :endDate
   ORDER BY j.createdAt ASC
   ```
3. 查出的 `Journal` 实体列表，再 stream 映射为 `CalendarDayResponse`：

```java
journals.stream()
    .map(j -> CalendarDayResponse.builder()
        .date(j.getCreatedAt().toLocalDate())
        .journalId(j.getId())
        .mood(j.getMood())
        .weather(j.getWeather())
        .title(j.getTitle())
        .build())
    .collect(Collectors.toList());
```

关键细节：`date` 字段取的是 `j.getCreatedAt().toLocalDate()`，即日记创建日期（去掉时分秒），而非 `Journal` 实体上某个独立的日期字段。这意味着同一天写多篇日记时，**列表中会出现多条相同 `date` 的记录**，每条对应一篇日记。

### 2.3 返回给前端的数据结构

`CalendarDayResponse` DTO：

| 字段 | 类型 | 含义 |
|---|---|---|
| `date` | `LocalDate` | 日记创建日期（如 2025-05-08） |
| `journalId` | `Long` | 日记 ID，前端可用来跳转详情 |
| `mood` | `String` | 心情，可能为 null |
| `weather` | `String` | 天气，可能为 null |
| `title` | `String` | 日记标题 |

返回的是 `ApiResponse<List<CalendarDayResponse>>`，JSON 大致为：

```json
{
  "success": true,
  "data": [
    {
      "date": "2025-05-01",
      "journalId": 12,
      "mood": "happy",
      "weather": "sunny",
      "title": "劳动节快乐"
    },
    {
      "date": "2025-05-03",
      "journalId": 13,
      "mood": "calm",
      "weather": "cloudy",
      "title": "安静的下午"
    }
  ]
}
```

**只返回有日记的日期**，没有日记的日子不会出现在列表中——由前端负责补齐空白日期的渲染。

### 2.4 为什么要单独搞 /calendar 接口，而不是让前端查 /journals 列表？

这个问题涉及数据量和数据形态两个维度：

1. **数据量不同**：`/journals` 是分页接口（`PageResponse`），默认每页 10 条，适合无限滚动浏览；而 `/calendar/{year}/{month}` 一次返回整月所有日记（通常几十条以内），不分页。日历视图需要一次拿到一个月的完整数据来渲染日历格，如果用分页接口前端得翻页拼凑，既慢又复杂。

2. **数据形态不同**：`/journals` 返回的 `JournalResponse` 包含 `content` 等大字段，适合列表浏览和详情查看；而 `/calendar` 返回的 `CalendarDayResponse` 是精简的，只有 `date`、`journalId`、`mood`、`weather`、`title`，**不包含 `content`**。日历格上只需要一个小标记（比如心情图标），加载正文既浪费流量也无意义。

3. **查询维度不同**：`/journals` 按 `createdAt DESC` 排序（最新优先）；`/calendar` 按 `createdAt ASC` 排序（日历顺序），且必须按月份范围过滤。`JournalRepository.findByUserIdAndMonth()` 就是为日历场景专门写的查询方法。

4. **关注点分离**：`JournalController` 负责 CRUD + 搜索，`CalendarController` 负责日历视图的只读聚合查询。职责清晰，接口语义明确，前端调用时一看 URL 就知道用途。

---

## 三、整体设计思路小结

- **没有独立的 StatsService**：统计逻辑直接写在 `JournalService.getStats()` 里。原因很直观——统计的底层数据全部来自 `Journal` 实体，不需要跨 Service 协调，单独建一个类反而增加跳转成本。
- **Repository 层分工清晰**：`JournalRepository` 定义了三个统计相关方法——`countByUserIdAndDeletedAtIsNull()`（派生查询）、`countByUserIdAndMonth()`（自定义 @Query）、`findMostFrequentMood()`（自定义 @Query），加上日历专用的 `findByUserIdAndMonth()`。每个方法对应一个明确的业务场景。
- **软删除一致性**：所有查询都带 `deletedAt IS NULL` 条件，确保已软删除的日记不参与统计和日历展示。
- **时间区间统一用左闭右开**：本月统计和日历查询都使用 `[start, end)` 模式，由 `YearMonth` 计算边界，避免时间边界漏算或重复。
