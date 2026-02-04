package com.dailyjournal.config;

import com.dailyjournal.entity.Journal;
import com.dailyjournal.entity.User;
import com.dailyjournal.repository.JournalRepository;
import com.dailyjournal.repository.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Data Initializer
 * 初始化测试数据 - 包含15天的日记mock数据
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final JournalRepository journalRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        // Check if test user already exists
        if (userRepository.existsByUsername("test")) {
            log.info("Test account already exists, skipping initialization");
            return;
        }

        log.info("Creating test account...");

        // Create test user
        User testUser = User.builder()
                .username("test")
                .email("test@example.com")
                .password(passwordEncoder.encode("123456"))
                .build();
        testUser = userRepository.save(testUser);

        log.info("Test account created: username=test, password=123456");

        // Create 15 days of sample journals
        createSampleJournals(testUser);

        log.info("15 days of sample journals created for test account");
    }

    private void createSampleJournals(User user) {
        LocalDateTime now = LocalDateTime.now();

        // Day 1 (Today)
        createJournal(user, "今天是美好的一天", 
            "<p>今天阳光明媚，我去公园散步了。</p><p>看到了很多美丽的花朵，心情很好！</p><p>希望每天都能这样开心。</p>",
            "happy", "sunny", now);

        // Day 2 (Yesterday)
        createJournal(user, "读了一本好书",
            "<p>今天读完了《小王子》，感触很深。</p><blockquote>真正重要的东西，用眼睛是看不见的。</blockquote><p>这句话让我想了很久，或许我们都太在意表面的东西了。</p>",
            "calm", "cloudy", now.minusDays(1));

        // Day 3
        createJournal(user, "下雨天的思绪",
            "<p>窗外下着雨，泡了一杯热茶。</p><p>听着雨声，思绪飘远...</p><p>想起了很多往事，有些怀念。</p>",
            "calm", "rainy", now.minusDays(2));

        // Day 4
        createJournal(user, "工作小有成就",
            "<p>今天终于完成了一个困扰很久的项目！</p><h2>收获</h2><ul><li>学会了新的技术</li><li>团队协作更顺畅了</li><li>获得了领导的认可</li></ul><p>继续加油！</p>",
            "happy", "sunny", now.minusDays(3));

        // Day 5
        createJournal(user, "和朋友的聚会",
            "<p>今天和老朋友们聚餐，聊了很多。</p><p>好久没有这么开心地笑了。</p><p>友情真的是人生中很珍贵的东西。</p>",
            "happy", "cloudy", now.minusDays(4));

        // Day 6
        createJournal(user, "有点焦虑的一天",
            "<p>最近事情有点多，感觉有些焦虑。</p><p>深呼吸，告诉自己：</p><blockquote>一切都会过去的，保持冷静。</blockquote><p>明天会更好。</p>",
            "anxious", "overcast", now.minusDays(5));

        // Day 7
        createJournal(user, "周末的悠闲时光",
            "<p>难得的周末，睡到自然醒。</p><h2>今天做了什么</h2><ol><li>看了两集喜欢的剧</li><li>做了一顿丰盛的午餐</li><li>下午小憩了一会儿</li></ol><p>这才是生活啊~</p>",
            "calm", "sunny", now.minusDays(6));

        // Day 8
        createJournal(user, "学习新技能",
            "<p>开始学习画画了！</p><p>虽然画得很丑，但是过程很有趣。</p><pre>今日练习：\n- 基础线条\n- 简单几何体\n- 光影初步</pre><p>期待自己的进步。</p>",
            "happy", "cloudy", now.minusDays(7));

        // Day 9
        createJournal(user, "想家了",
            "<p>看到朋友圈里家人的照片，突然很想家。</p><p>离家已经三个月了...</p><p>下个月一定要回去看看。</p>",
            "sad", "rainy", now.minusDays(8));

        // Day 10
        createJournal(user, "咖啡馆的午后",
            "<p>找了一家安静的咖啡馆，点了杯拿铁。</p><p>阳光透过玻璃窗洒进来，很温暖。</p><p>在这里写了一些东西，感觉很平静。</p>",
            "calm", "sunny", now.minusDays(9));

        // Day 11
        createJournal(user, "运动的快乐",
            "<p>今天去健身房了！</p><h2>训练内容</h2><ul><li>跑步30分钟</li><li>力量训练40分钟</li><li>拉伸15分钟</li></ul><p>虽然很累，但是很充实。运动真的能让人心情变好。</p>",
            "happy", "sunny", now.minusDays(10));

        // Day 12
        createJournal(user, "下雪了",
            "<p>今年第一场雪！</p><p>窗外白茫茫一片，好美。</p><p>小时候最喜欢下雪天了，可以堆雪人、打雪仗。</p><p>现在只能在窗边看看了。</p>",
            "happy", "snowy", now.minusDays(11));

        // Day 13
        createJournal(user, "有些疲惫",
            "<p>最近加班有点多，身体有些吃不消。</p><p>提醒自己：</p><blockquote>健康才是最重要的，工作是做不完的。</blockquote><p>今晚早点休息。</p>",
            "sad", "overcast", now.minusDays(12));

        // Day 14
        createJournal(user, "新的计划",
            "<p>新的一周开始了，制定了一些小目标：</p><ol><li>每天早起30分钟</li><li>坚持读书</li><li>减少刷手机时间</li><li>多喝水</li></ol><p>希望能坚持下去！</p>",
            "calm", "cloudy", now.minusDays(13));

        // Day 15
        createJournal(user, "感恩的心",
            "<p>今天突然想记录一下生活中值得感恩的事：</p><ul><li>有一份还不错的工作</li><li>有爱我的家人</li><li>有几个真心的朋友</li><li>身体健康</li></ul><p>其实仔细想想，拥有的已经很多了。</p><p>保持感恩的心，继续前行。</p>",
            "happy", "sunny", now.minusDays(14));
    }

    private void createJournal(User user, String title, String content, 
                               String mood, String weather, LocalDateTime createdAt) {
        Journal journal = Journal.builder()
                .user(user)
                .title(title)
                .content(content)
                .mood(mood)
                .weather(weather)
                .build();
        
        journal = journalRepository.save(journal);
        
        // Update created_at using native query to set specific date
        entityManager.createNativeQuery(
            "UPDATE journals SET created_at = :createdAt, updated_at = :createdAt WHERE id = :id")
            .setParameter("createdAt", createdAt)
            .setParameter("id", journal.getId())
            .executeUpdate();
    }
}
