package com.cf.smartq.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cf.smartq.common.ErrorCode;
import com.cf.smartq.constant.CommonConstant;
import com.cf.smartq.exception.ThrowUtils;
import com.cf.smartq.mapper.ScoringResultMapper;
import com.cf.smartq.model.dto.result.ScoringResultQueryRequest;
import com.cf.smartq.model.entity.ScoringResult;
import com.cf.smartq.model.entity.User;
import com.cf.smartq.model.vo.ScoringResultVO;
import com.cf.smartq.model.vo.UserVO;
import com.cf.smartq.service.ScoringResultService;
import com.cf.smartq.service.UserService;
import com.cf.smartq.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 評分結果服務實現
 */
@Service
@Slf4j
public class ScoringResultServiceImpl extends ServiceImpl<ScoringResultMapper, ScoringResult> implements ScoringResultService {

    @Resource
    private UserService userService;

    /**
     * 校驗數據
     *
     * @param scoringresult
     * @param add 對創建的數據進行校驗
     */
    @Override
    public void validScoringResult(ScoringResult scoringresult, boolean add) {
        // 已使用註解校驗，此處不再需要手動校驗
    }

    /**
     * 獲取查詢條件
     *
     * @param scoringresultQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<ScoringResult> getQueryWrapper(ScoringResultQueryRequest scoringresultQueryRequest) {
        QueryWrapper<ScoringResult> queryWrapper = new QueryWrapper<>();
        if (scoringresultQueryRequest == null) {
            return queryWrapper;
        }

        Long id = scoringresultQueryRequest.getId();
        String resultName = scoringresultQueryRequest.getResultName();
        String resultDesc = scoringresultQueryRequest.getResultDesc();
        Integer resultScoreRange = scoringresultQueryRequest.getResultScoreRange();
        Long appId = scoringresultQueryRequest.getAppId();
        Long userId = scoringresultQueryRequest.getUserId();
        String searchText = scoringresultQueryRequest.getSearchText();

        // 模糊查詢
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.and(qw -> qw.like("resultName", searchText).or().like("resultDesc", searchText));
        }

        // 精確查詢（因為已經在 DTO 中使用了 @NotNull 註解，這裡可以直接使用）
        if (id != null) {
            queryWrapper.eq("id", id);
        }
        if (userId != null) {
            queryWrapper.eq("userId", userId);
        }
        if (appId != null) {
            queryWrapper.eq("appId", appId);
        }
        if (resultScoreRange != null) {
            queryWrapper.eq("resultScoreRange", resultScoreRange);
        }

        return queryWrapper;
    }

    @Override
    public ScoringResultVO getScoringResultVO(ScoringResult scoringresult, HttpServletRequest request) {
        ScoringResultVO scoringresultVO = ScoringResultVO.objToVo(scoringresult);
        Long userId = scoringresult.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        scoringresultVO.setUserVO(userVO);
        return scoringresultVO;
    }

    @Override
    public Page<ScoringResultVO> getScoringResultVOPage(Page<ScoringResult> scoringresultPage, HttpServletRequest request) {
        List<ScoringResult> scoringresultList = scoringresultPage.getRecords();
        Page<ScoringResultVO> scoringresultVOPage = new Page<>(scoringresultPage.getCurrent(), scoringresultPage.getSize(), scoringresultPage.getTotal());
        if (CollUtil.isEmpty(scoringresultList)) {
            return scoringresultVOPage;
        }
        List<ScoringResultVO> scoringresultVOList = scoringresultList.stream()
                .map(scoringresult -> getScoringResultVO(scoringresult, request))
                .collect(Collectors.toList());
        scoringresultVOPage.setRecords(scoringresultVOList);
        return scoringresultVOPage;
    }
}
