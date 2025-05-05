package com.cf.smartq.scoring;

import com.cf.smartq.common.ErrorCode;
import com.cf.smartq.exception.BusinessException;
import com.cf.smartq.model.entity.App;
import com.cf.smartq.model.entity.UserAnswer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
@Slf4j
public class ScoringStrategyExecutor {

    // 策略列表
    @Resource
    private List<ScoringStrategy> scoringStrategyList;

    /**
     * 评分
     *
     * @param choiceList
     * @param app
     * @return
     * @throws Exception
     */
    public UserAnswer doScore(List<String> choiceList, App app) throws Exception {


        Integer appType = app.getAppType();
        Integer appScoringStrategy = app.getScoringStrategy();


        // 添加日志输出
        log.info("尝试匹配评分策略 - appType: {}, scoringStrategy: {}", appType, appScoringStrategy);
        log.info("已注册的策略数量: {}", scoringStrategyList.size());

        // 输出所有已注册的策略
        for (ScoringStrategy strategy : scoringStrategyList) {
            if (strategy.getClass().isAnnotationPresent(ScoringStrategyConfig.class)) {
                ScoringStrategyConfig config = strategy.getClass().getAnnotation(ScoringStrategyConfig.class);
                log.info("已注册策略: {} - appType: {}, scoringStrategy: {}",
                        strategy.getClass().getSimpleName(),
                        config.appType(),
                        config.scoringStrategy());
            } else {
                log.info("策略未标注配置: {}", strategy.getClass().getSimpleName());
            }
        }
        if (appType == null || appScoringStrategy == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "应用配置有误，未找到匹配的策略");
        }
        // 根据注解获取策略
        for (ScoringStrategy strategy : scoringStrategyList) {
            if (strategy.getClass().isAnnotationPresent(ScoringStrategyConfig.class)) {
                ScoringStrategyConfig scoringStrategyConfig = strategy.getClass().getAnnotation(ScoringStrategyConfig.class);
                if (scoringStrategyConfig.appType() == appType && scoringStrategyConfig.scoringStrategy() == appScoringStrategy) {
                    return strategy.doscore(app,choiceList);
                }
            }
        }
        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "应用配置有误，未找到匹配的策略");
    }
}
