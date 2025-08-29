package com.cf.smartq.scoring;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cf.smartq.model.entity.App;
import com.cf.smartq.model.entity.ScoringResult;
import com.cf.smartq.model.entity.UserAnswer;

import java.util.List;

/**
 * 评分策略接口
 */
public interface ScoringStrategy extends IService<ScoringResult> {
    /**
     * 开始评分
     */
    UserAnswer doscore(App app, List<String> Choices);

}
