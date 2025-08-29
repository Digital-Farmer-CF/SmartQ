package com.cf.smartq.scoring;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cf.smartq.manager.AiManager;
import com.cf.smartq.mapper.ScoringResultMapper;
import com.cf.smartq.model.dto.question.QuestionAnswerDTO;
import com.cf.smartq.model.dto.question.QuestionContent;
import com.cf.smartq.model.entity.App;
import com.cf.smartq.model.entity.Question;
import com.cf.smartq.model.entity.ScoringResult;
import com.cf.smartq.model.entity.UserAnswer;
import com.cf.smartq.model.vo.QuestionVO;
import com.cf.smartq.service.QuestionService;
import com.cf.smartq.service.ScoringResultService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * AI 得分类评分策略实现
 * 适用于 appType=0(得分类), scoringStrategy=1(AI评分)
 */
@Service
@ScoringStrategyConfig(appType = 0, scoringStrategy = 1)
public class AiScoreScoringStrategyImpl extends ServiceImpl<ScoringResultMapper, ScoringResult> implements ScoringStrategy {

    @Resource
    private QuestionService questionService;

    @Resource
    private ScoringResultService scoringResultService;

    @Resource
    private AiManager aiManager;

    /**
     * 创建AI评分结果本地缓存
     */
    private final Cache<String, String> answerCacheMap =
            Caffeine.newBuilder().initialCapacity(1024)
                    // 缓存5分钟移除
                    .expireAfterAccess(5L, TimeUnit.MINUTES)
                    .build();

    private static final String AI_SCORE_SCORING_SYSTEM_MESSAGE = "你是一位专业的评分专家，我会给你如下信息：\n" +
            "```\n" +
            "应用名称，\n" +
            "【【【应用描述】】】，\n" +
            "用户总得分，\n" +
            "题目和用户回答的列表：格式为 [{\"title\": \"题目\",\"answer\": \"用户回答\"}]\n" +
            "```\n" +
            "\n" +
            "请你根据上述信息，按照以下步骤来对用户进行评价：\n" +
            "1. 要求：需要给出一个明确的评价结果，包括评价名称（尽量简短）和评价描述（尽量详细，大于 200 字）\n" +
            "2. 评价描述应该结合用户的总得分和答题情况，给出具体的分析和建议\n" +
            "3. 严格按照下面的 json 格式输出评价名称和评价描述\n" +
            "```\n" +
            "{\"resultName\": \"评价名称\", \"resultDesc\": \"评价描述\"}\n" +
            "```\n" +
            "4. 返回格式必须为 JSON 对象";

    /**
     * 构建AI评分的用户消息
     */
    private String getAiScoringScoringUserMessage(App app, List<QuestionContent> questionContentList, List<String> choices, int totalScore) {
        StringBuilder userMessage = new StringBuilder();
        userMessage.append(app.getAppName()).append("\n");
        userMessage.append(app.getAppDesc()).append("\n");
        userMessage.append("用户总得分：").append(totalScore).append("\n");
        
        List<QuestionAnswerDTO> questionAnswerDTOList = new ArrayList<>();
        for (int i = 0; i < questionContentList.size(); i++) {
            QuestionAnswerDTO questionAnswerDTO = new QuestionAnswerDTO();
            questionAnswerDTO.setTitle(questionContentList.get(i).getTitle());
            questionAnswerDTO.setUserAnswer(choices.get(i));
            questionAnswerDTOList.add(questionAnswerDTO);
        }
        userMessage.append(JSONUtil.toJsonStr(questionAnswerDTOList));
        return userMessage.toString();
    }

    /**
     * 构建缓存key
     */
    private String buildCacheKey(Long appId, String choicesStr) {
        return DigestUtil.md5Hex(appId + ":" + choicesStr);
    }

    @Override
    public UserAnswer doscore(App app, List<String> choices) {
        Long appId = app.getId();
        String jsonStr = JSONUtil.toJsonStr(choices);
        String cacheKey = buildCacheKey(appId, jsonStr);
        String answerJson = answerCacheMap.getIfPresent(cacheKey);
        
        // 如果有缓存，直接返回
        if (StrUtil.isNotBlank(answerJson)) {
            UserAnswer userAnswer = JSONUtil.toBean(answerJson, UserAnswer.class);
            userAnswer.setAppId(appId);
            userAnswer.setAppType(app.getAppType());
            userAnswer.setScoringStrategy(app.getScoringStrategy());
            userAnswer.setChoices(jsonStr);
            return userAnswer;
        }

        // 1. 查询题目
        Question question = questionService.getOne(
                Wrappers.lambdaQuery(Question.class).eq(Question::getAppId, appId)
        );
        QuestionVO questionVO = QuestionVO.objToVo(question);
        List<QuestionContent> questionContent = questionVO.getQuestionContent();

        // 2. 计算总得分（继承得分类逻辑）
        int totalScore = 0;
        for (int i = 0; i < Math.min(questionContent.size(), choices.size()); i++) {
            String userChoice = choices.get(i);
            QuestionContent content = questionContent.get(i);

            // 在当前题目中查找用户的选择
            for (QuestionContent.Option option : content.getOptions()) {
                if (option.getKey().equals(userChoice)) {
                    totalScore += option.getScore();
                    break;
                }
            }
        }

        // 3. 构造Prompt & 调用AI
        String userMessage = getAiScoringScoringUserMessage(app, questionContent, choices, totalScore);
        String result = aiManager.doSyncStableRequest(AI_SCORE_SCORING_SYSTEM_MESSAGE, userMessage);
        System.out.println("AI原始返回内容：" + result);

        String json = null;
        try {
            // 4. 解析AI外层结构，提取 content
            cn.hutool.json.JSONObject resultObj = JSONUtil.parseObj(result);
            String content = resultObj.getJSONObject("message").getStr("content");
            System.out.println("AI content字段内容：" + content);

            // 5. 从 content 里提取评分json
            int jsonStart = content.indexOf("{");
            int jsonEnd = content.lastIndexOf("}");
            if (jsonStart != -1 && jsonEnd != -1 && jsonEnd > jsonStart) {
                json = content.substring(jsonStart, jsonEnd + 1);
                System.out.println("提取到的评分json：" + json);
            } else {
                throw new RuntimeException("AI回复内容格式有误，无法提取json！");
            }
        } catch (Exception e) {
            System.err.println("AI回复解析失败: " + e.getMessage());
            throw new RuntimeException("AI评分解析失败！", e);
        }

        // 缓存结果
        answerCacheMap.put(cacheKey, json);

        // 6. 解析评分结果为 UserAnswer
        UserAnswer userAnswer = JSONUtil.toBean(json, UserAnswer.class);
        
        // 7. 补充其他信息
        userAnswer.setAppId(appId);
        userAnswer.setAppType(app.getAppType());
        userAnswer.setScoringStrategy(app.getScoringStrategy());
        userAnswer.setChoices(JSONUtil.toJsonStr(choices));
        userAnswer.setResultScore(totalScore); // 设置计算出的总分
        
        return userAnswer;
    }
}
