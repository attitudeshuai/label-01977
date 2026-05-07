# StatsController 与 CalendarController 分析文档

## 目录

1. [StatsController 统计功能分析](#1-statscontroller-统计功能分析)
2. [CalendarController 日历功能分析](#2-calendarcontroller-日历功能分析)

---

## 1. StatsController 统计功能分析

### 1.1 入口与调用链路

StatsController 只有一个接口，路由是 `GET /stats/overview`，方法名是 `getOverview`。它做的事情很简单：从当前登录用户的 JWT 中拿到 `userId`，然后调用 `journalService.getStats(userId)` 去计算统计数据，最后把结果包一层 `ApiResponse` 返回给前端。

完整调用链：

```
StatsController.getOverview() 
    → JournalService.getStats(userId)
        → JournalRepository 的三个查询方法
```

### 1.2 getStats() 的三个统计项详解

`getStats()` 方法依次执行了三段逻辑，分别对应 `StatsResponse` 里的四个字段。

#### 1.2.1 日记总数（totalJournals）

第一个统计项最简单，调用的是 `JournalRepository.countByUserIdAndDeletedAtIsNull(userId)`。

这是一个 Spring Data JPA 的命名查询方法，根据方法名就能猜出它的含义：按当前用户统计，并且只算那些 `deletedAt` 为空的记录。换句话说，它统计的是用户所有"未被软删除"的日记总数。

在 Service 层拿到计数后，直接赋给 `StatsResponse.totalJournals`。

#### 1.2.2 本月统计（thisMonthJournals）

第二个统计项计算的是"当前月份写了多少篇日记"。

Service 层先用 `YearMonth.now()` 拿到系统当前年月，然后构造两个时间点：`startOfMonth` 是本月 1 号零点，`startOfNextMonth` 是下月 1 号零点。接着调用 `JournalRepository.countByUserIdAndMonth(userId, startOfMonth, startOfNextMonth)`。

Repository 层这个方法用的是自定义 JPQL 查询，查询条件包含了时间区间判断：`j.createdAt >= :startDate AND j.createdAt < :endDate`。这里用的是"左闭右开"的写法——包含本月 1 号零点，但不包含下月 1 号零点。这样写的好处是不用管这个月到底有几天，28、29、30、31 天统统适用，也不用考虑 23:59:59 这种毫秒精度的边界问题。

拿到计数后，赋给 `StatsResponse.thisMonthJournals`。

#### 1.2.3 最多的心情（topMood + topMoodCount）

第三个统计项稍微复杂一点，涉及到聚合查询。

调用的是 `JournalRepository.findMostFrequentMood(userId)`。这个 JPQL 查询做了几件事：首先按 `j.mood` 字段分组，然后用 `COUNT(j.mood)` 统计每种心情出现了多少次，接着过滤掉 `mood` 为空的记录，最后按统计结果降序排列。

Repository 方法的返回类型是 `List<Object[]>`，列表里的每一个元素代表一种心情及其计数；因为按次数降序排过，所以列表第一条就是出现次数最多的那个心情。

回到 Service 层，代码只取了列表的第一条记录。具体做法是：如果列表非空，就把第一个元素的 `[0]` 号位置强转为 `String` 作为 `topMood`，把 `[1]` 号位置强转为 `Long` 作为 `topMoodCount`；如果列表为空（说明没有日记，或者所有日记都没设置心情），就把这两个值分别设为 `null` 和 `0`。

这两个值最后分别赋给 `StatsResponse.topMood` 和 `StatsResponse.topMoodCount`。

### 1.3 关于"心情分布"的重要说明

这里有一个容易产生误解的地方，需要特别说明一下。

从 Repository 查询本身来看，`findMostFrequentMood` 返回的是一个列表，理论上包含了所有心情的排名信息——如果用户有 happy、calm、sad 三种心情，这个列表里应该有三条记录，分别告诉我们每种心情各出现了多少次。

但 Service 层的代码只取了第一条：`moodStats.get(0)`。后面的元素完全被忽略了。

这意味着：

- 系统**没有**返回完整的心情分布。前端拿不到"happy 占 40%、calm 占 30%、sad 占 20%"这种完整分布数据。
- 系统**只**返回了"第一名是谁、出现了多少次"。
- `StatsResponse` 这个 DTO 也只有 `topMood` 和 `topMoodCount` 两个字段，没有为"完整分布"预留列表类型的字段。

所以准确地说，这里不是"心情分布"，而是"最多心情"。要实现真正的分布统计（比如饼图所需的各心情占比），需要在 DTO 里增加一个列表字段，并且 Service 层要把整个 `moodStats` 列表都转换出来返回给前端，而不是只取第一条。

### 1.4 返回给前端的数据结构

最终组装成的 `StatsResponse` 包含四个字段：

- `totalJournals`：所有未删除的日记总数
- `thisMonthJournals`：本月写的日记数
- `topMood`：出现次数最多的心情类型（可能为 null）
- `topMoodCount`：该心情出现的次数

Service 层用 Builder 模式组装好后，再由 Controller 包一层 `ApiResponse` 返回。

---

## 2. CalendarController 日历功能分析

### 2.1 入口与调用链路

CalendarController 也只有一个接口，路由是 `GET /calendar/{year}/{month}`，方法名是 `getCalendarMonth`。前端通过路径参数传入年份和月份，比如 `/calendar/2026/5` 就是查 2026 年 5 月的日历数据。

Controller 里先做了一个简单的参数校验：如果月份小于 1 或大于 12，直接返回 400 Bad Request。校验通过后，调用 `journalService.getCalendarMonth(userId, year, month)`。

完整调用链：

```
CalendarController.getCalendarMonth(year, month)
    → JournalService.getCalendarMonth(userId, year, month)
        → JournalRepository.findByUserIdAndMonth
```

### 2.2 getCalendarMonth() 的执行逻辑

`getCalendarMonth()` 方法可以分成三步来看。

第一步是根据传入的 `year` 和 `month` 构造时间边界，这和 getStats 里的做法类似：用 `YearMonth.of(year, month)` 得到指定年月，然后算出当月第一天零点和下月第一天零点。

第二步是调用 Repository 层的 `findByUserIdAndMonth` 方法，查询这个时间区间内的所有日记。这个方法返回的是 `List<Journal>`，也就是完整的 Journal 实体列表，包含了实体上的所有字段。排序方式是按 `createdAt` 正序，即从月初到月末排列。

第三步是用 Stream 把 `List<Journal>` 转换成 `List<CalendarDayResponse>`。转换过程中，每条 Journal 只提取五个字段：把 `createdAt` 转成 `LocalDate` 作为 `date`，`id` 作为 `journalId`，然后是 `mood`、`weather` 和 `title`。而 `content` 这种大文本字段在这里被丢弃了，因为日历视图用不上。

### 2.3 返回给前端的数据结构

返回给前端的是 `List<CalendarDayResponse>`，每个元素包含五个字段：

- `date`：日记日期
- `journalId`：日记 ID
- `mood`：心情
- `weather`：天气
- `title`：标题

需要注意的是，这个列表只包含"有日记的那些天"。如果用户 5 月只写了 5 篇日记，返回的列表就只有 5 条记录，而不是 31 条。没写日记的日子不会出现占位项，这个填充逻辑由前端自己处理。

### 2.4 为什么要单独搞个 calendar 接口

这是一个关于接口职责划分的设计问题。要理解为什么不直接让前端查 journal 列表，需要先看一下现有的 journal 列表接口是什么样的。

Journal 列表走的是 `JournalService.getJournals(userId, page, size)`。这个接口的特点是：它是分页的，用 `PageRequest` 控制每次取多少条；它按 `createdAt` 倒序排列，最新的日记在最前面；它返回的是完整的 `JournalResponse`，包括 `content` 在内的所有信息。

而日历场景的需求完全不同。日历视图需要的是整月的数据，不能分页——一个月也就 30 来天，一次请求就应该把所有有日记的日子都拿到。日历也不需要 `content` 这种大字段，格子里只显示心情颜色、天气图标和标题就够了。日历还需要正序排列，从月初到月末，方便前端按日期渲染。

如果让前端直接调用 journal 列表接口，会遇到几个问题。首先是分页，前端要么把 size 设成一个很大的数（比如 1000），这违背了分页接口的设计初衷；要么得发好几次请求自己合并数据。其次是数据量的问题，`content` 字段可能很长，白白浪费带宽。第三是排序方向不对，前端拿到倒序的数据还得自己再排一遍。第四是语义上的混乱，前端为了渲染日历却去调"日记列表"接口，职责不清晰。

单独的 calendar 接口正好解决了这些问题。它不分页，一次返回整月数据；它只返回日历真正需要的精简字段；它的排序就是前端需要的正序；它的接口名称和路由都明确表达了"这是日历专用接口"的语义。这种"按场景拆分接口"的做法，虽然多写了一个 Controller 和一个 Service 方法，但换来的是更清晰的职责、更易维护的代码和更好的性能。

### 2.5 一个潜在的优化点

当前实现有一个可以进一步优化的地方。

看 Service 层的代码：Repository 返回的是完整的 `Journal` 实体列表，包括 `content` 字段；但 Stream 转换时只用了五个字段，`content` 直接被扔掉了。

这意味着 JPA 从数据库把所有字段都取出来了，包括可能很长的 `content`，但应用层只用了一小部分。对于数据量不大的个人日记应用来说，这个问题通常不明显；但如果 `content` 字段存了很多长篇内容，就会造成不必要的数据库 IO 和内存消耗。

优化方向是使用 JPA 的 DTO Projection 或 Interface Projection，让 Repository 在查询层面就只选择需要的字段，而不是把整个实体查出来再在内存里裁剪。不过当前的实现以"代码清晰"为优先，功能是正确的，这个优化点不影响对整体逻辑的理解。

---

## 总结

### Stats 模块

getStats 方法做了三件事：

- 调用 `countByUserIdAndDeletedAtIsNull` 统计未删除的日记总数，放到 `totalJournals`
- 构造当月的时间区间，调用 `countByUserIdAndMonth` 统计本月日记数，放到 `thisMonthJournals`
- 调用 `findMostFrequentMood` 做聚合查询，**但只取第一条**，把最多的心情和次数分别放到 `topMood` 和 `topMoodCount`

关于第三点需要再次强调：系统目前没有返回完整的心情分布，只返回了排名第一的心情和它的次数。如果未来需要做饼图之类的分布展示，需要在 DTO 中增加列表字段，并且 Service 层要遍历整个 `moodStats` 列表而不是只取 `get(0)`。

### Calendar 模块

getCalendarMonth 方法：

- 根据 `year` 和 `month` 构造时间边界
- 调用 `findByUserIdAndMonth` 查询当月所有日记
- 用 Stream 转换成 `List<CalendarDayResponse>`，只保留日历需要的字段

单独 calendar 接口的设计原因：

- journal 列表接口是分页 + 倒序 + 完整字段
- 日历场景需要的是不分页 + 正序 + 精简字段
- 两个场景的需求差异很大，强行共用一个接口会带来语义混乱和性能损耗

### 两个模块的共同特点

- 所有数据访问都走 `JournalRepository`
- 所有查询都过滤了 `deletedAt IS NULL`，遵循系统的软删除策略
- 所有查询都按 `userId` 过滤，保证用户数据隔离
- 全部使用 Spring Data JPA，没有 MyBatis
