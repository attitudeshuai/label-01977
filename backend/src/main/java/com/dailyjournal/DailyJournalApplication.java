package com.dailyjournal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Daily Journal Application
 * 日记记录系统 - Spring Boot 启动类
 */
@SpringBootApplication
public class DailyJournalApplication {

    public static void main(String[] args) {
        SpringApplication.run(DailyJournalApplication.class, args);
        
        // Startup success log
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🌸 心情随笔 Backend Started Successfully!");
        System.out.println("=".repeat(60));
        System.out.println("📍 API Base URL: http://localhost:8080/api");
        System.out.println("📍 Health Check: http://localhost:8080/api/health");
        System.out.println("📍 Test Account: username=test, password=123456");
        System.out.println("=".repeat(60) + "\n");
    }
}
