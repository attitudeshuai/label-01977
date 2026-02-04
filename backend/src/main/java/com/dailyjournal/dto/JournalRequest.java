package com.dailyjournal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Journal Request DTO
 * 日记请求 DTO
 */
@Data
public class JournalRequest {

    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题长度不能超过200字符")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;

    @Size(max = 20, message = "心情标签长度不能超过20字符")
    private String mood; // happy, calm, sad, angry, anxious

    @Size(max = 20, message = "天气标签长度不能超过20字符")
    private String weather; // sunny, cloudy, overcast, rainy, snowy
}
