package com.cf.smartq.model.dto.question;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionContent {
    // 题目内容
    private String title;
    // 选项列表
    private List<Option> options;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Option {
        // 如果是测评类，则用 result 来保存答案属性
        private String result;
        // 如果是得分类，则用 score 来设置本题分数
        private int score;
        // 选项内容
        private String value;
        // 选项 key
        private String key;
    }
}