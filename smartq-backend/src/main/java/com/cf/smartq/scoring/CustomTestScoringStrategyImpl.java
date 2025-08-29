package com.cf.smartq.scoring;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cf.smartq.mapper.ScoringResultMapper;
import com.cf.smartq.model.dto.question.QuestionContent;
import com.cf.smartq.model.entity.App;
import com.cf.smartq.model.entity.Question;
import com.cf.smartq.model.entity.ScoringResult;
import com.cf.smartq.model.entity.UserAnswer;
import com.cf.smartq.model.vo.QuestionVO;
import com.cf.smartq.service.QuestionService;
import com.cf.smartq.service.ScoringResultService;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 自定义评分-测试类
 */
@ScoringStrategyConfig(appType = 1, scoringStrategy = 0)
public class CustomTestScoringStrategyImpl extends ServiceImpl<ScoringResultMapper, ScoringResult> implements ScoringStrategy {
    @Resource
    private QuestionService questionService;

    @Resource
    private ScoringResultService scoringResultService;

    @Override
    public UserAnswer doscore(App app, List<String> Choices) {
        //得到QuestionContent->才能知道用户所选的答案对应的(I或者E)
        Question question = questionService.lambdaQuery()
                .eq(Question::getAppId, app.getId())
                .one();
        //得到预先设计好的ScoringResult->才能根据得到的答案(I或者E)给相应的人格加分
        List<ScoringResult> scoringResultlist = scoringResultService.lambdaQuery()
                .eq(ScoringResult::getAppId, app.getId())
                .list();
        //初始化一个Map,用户统计属性的个数(I几个,E几个)
        Map<String, Integer> optionCount = new HashMap<>();
        //遍历用户的Choices,得到答案
        QuestionVO questionVO = QuestionVO.objToVo(question);
        List<QuestionContent> questionContent = questionVO.getQuestionContent();

        // 遍历用户选择和问题内容，统计各选项结果
        for (String choice : Choices) {
            for (QuestionContent content : questionContent) {
                for (QuestionContent.Option option : content.getOptions()) {
                    if (option.getKey().equals(choice)) {
                        //获取选项的result属性
                        String result = option.getResult();
                        //如果result属性不在optionCount中,初始化为0
                        if (!optionCount.containsKey(result)) {
                            optionCount.put(result, 0);
                        }
                        //如果result属性在optionCount中,则加1
                        optionCount.put(result, optionCount.get(result) + 1);
                    }
                }
            }
        }

        //遍历每种评分结果,计算哪个结果的得分更高
        //初始化最高分数和最高分数对应的评分结果
        int maxScore = 0;
        ScoringResult maxScoreResult = scoringResultlist.get(0);
        //遍历评分结果
        for (ScoringResult scoringResult : scoringResultlist) {
            List<String> resultProp = JSONUtil.toList(scoringResult.getResultProp(), String.class);
            //计算当前评分结果的分数   [I,E] => [10,5] => 15
            int score = resultProp.stream()
                    .mapToInt(prop -> optionCount.getOrDefault(prop, 0))
                    .sum();
            //如果当前评分结果的分数比最高分数高,则更新最高分数和最高分数对应的评分结果
            if (score > maxScore) {
                maxScore = score;
                maxScoreResult = scoringResult;
            }
        }

        //构造返回值,填充答案对象的属性
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setAppId(app.getId());
        userAnswer.setAppType(app.getAppType());
        userAnswer.setScoringStrategy(app.getScoringStrategy());
        userAnswer.setChoices(JSONUtil.toJsonStr(Choices));
        userAnswer.setResultName(maxScoreResult.getResultName());
        userAnswer.setResultDesc(maxScoreResult.getResultDesc());
        userAnswer.setResultPicture(maxScoreResult.getResultPicture());
        return userAnswer;
    }
}