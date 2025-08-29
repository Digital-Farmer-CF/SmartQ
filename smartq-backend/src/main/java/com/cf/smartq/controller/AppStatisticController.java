package com.cf.smartq.controller;

import com.cf.smartq.common.BaseResponse;
import com.cf.smartq.common.ErrorCode;
import com.cf.smartq.common.ResultUtils;
import com.cf.smartq.exception.ThrowUtils;
import com.cf.smartq.mapper.UserAnswerMapper;
import com.cf.smartq.model.dto.statistic.AppAnswerCountDTO;
import com.cf.smartq.model.dto.statistic.UserResultCountDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/app/statistic")
@Slf4j
public class AppStatisticController {

    @Resource
    private UserAnswerMapper userAnswerMapper;

    /**
     * 统计应用回答数
     * @return
     */
    @GetMapping("/answer_count")
    public BaseResponse<List<AppAnswerCountDTO>> getAppAnswerCount() {
        return ResultUtils.success(userAnswerMapper.doAppAnswerCount());
    }

    /**
     * 统计回答分布
     */
    @GetMapping("/answer_result_count")
    public BaseResponse<List<UserResultCountDTO>> getAppAnswerResultCount(Long appId) {
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(userAnswerMapper.doAppAnswerResultCount(appId));
    }
}



