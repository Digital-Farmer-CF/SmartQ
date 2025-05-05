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
import java.util.jar.JarEntry;

@ScoringStrategyConfig(appType = 0, scoringStrategy = 0)
public class CostumScoreScoringStrategyImpl extends ServiceImpl<ScoringResultMapper, ScoringResult> implements ScoringStrategy {
    @Resource
    private QuestionService questionService;

    @Resource
    private ScoringResultService scoringResultService;

    //开始评分
    @Override
    public UserAnswer doscore(App app, List<String> Choices) {
        //1.根据id查询到题目和题目结构信息(按分数降序排序)
        //得到QuestionContent->才能知道用户所选的答案对应的(I或者E)
        Question question = questionService.lambdaQuery()
                .eq(Question::getAppId, app.getId())
                .one();
        //得到预先设计好的ScoringResult->才能根据得到的答案(I或者E)给相应的人格加分
        List<ScoringResult> scoringResultlist = scoringResultService.lambdaQuery()
                .eq(ScoringResult::getAppId, app.getId())
                .orderByDesc(ScoringResult::getResultScoreRange)
                .list();
        //2.统计用户的总得分
        //定义得分计数器
        int totalscore = 0;
        //遍历用户的Choices,得到答案
        QuestionVO questionVO = QuestionVO.objToVo(question);
        List<QuestionContent> questionContent = questionVO.getQuestionContent();

        // 遍历用户选择和问题内容，统计各选项结果
        // 遍历题目和对应的用户选择
        for (int i = 0; i < Math.min(questionContent.size(), Choices.size()); i++) {
            String userChoice = Choices.get(i);         // 用户的第i个选择
            QuestionContent content = questionContent.get(i);  // 第i道题

            // 在当前题目中查找用户的选择
            for (QuestionContent.Option option : content.getOptions()) {
                if (option.getKey().equals(userChoice)) {
                    // 找到匹配的选项，加分
                    totalscore += option.getScore();
                    break;  // 找到后就停止查找此题的其他选项
                }
            }
        }

        //3.遍历得分结果,找到第一个用户分数大于得分范围的结果,作为最终结果
        ScoringResult maxScoreResult = scoringResultlist.get(0);
        for (ScoringResult scoringResult : scoringResultlist) {
            if(totalscore >= scoringResult.getResultScoreRange()){
                maxScoreResult =scoringResult;
                break;
            }
        }
        //4.构造返回值,填充对象的属性

        //构造返回值,填充答案对象的属性
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setAppId(app.getId());
        userAnswer.setAppType(app.getAppType());
        userAnswer.setScoringStrategy(app.getScoringStrategy());
        userAnswer.setChoices(JSONUtil.toJsonStr(Choices));
        userAnswer.setResultName(maxScoreResult.getResultName());
        userAnswer.setResultDesc(maxScoreResult.getResultDesc());
        userAnswer.setResultPicture(maxScoreResult.getResultPicture());
        userAnswer.setResultScore(totalscore);
        return userAnswer;
    }
}
