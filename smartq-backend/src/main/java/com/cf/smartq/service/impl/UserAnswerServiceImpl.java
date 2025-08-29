package com.cf.smartq.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cf.smartq.common.ErrorCode;
import com.cf.smartq.constant.CommonConstant;
import com.cf.smartq.exception.ThrowUtils;
import com.cf.smartq.mapper.UserAnswerMapper;
import com.cf.smartq.model.dto.useranswer.UserAnswerQueryRequest;
import com.cf.smartq.model.entity.UserAnswer;
import com.cf.smartq.model.entity.User;
import com.cf.smartq.model.vo.UserAnswerVO;
import com.cf.smartq.model.vo.UserVO;
import com.cf.smartq.service.UserAnswerService;
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
 * 用戶答題服務實現
 */
@Service
@Slf4j
public class UserAnswerServiceImpl extends ServiceImpl<UserAnswerMapper, UserAnswer> implements UserAnswerService {

    @Resource
    private UserService userService;

    /**
     * 校驗數據
     *
     * @param useranswer
     * @param add 對創建的數據進行校驗
     */
    @Override
    public void validUserAnswer(UserAnswer useranswer, boolean add) {
        // 已使用註解校驗，此處不再需要手動校驗
    }

    /**
     * 獲取查詢條件
     *
     * @param useranswerQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<UserAnswer> getQueryWrapper(UserAnswerQueryRequest useranswerQueryRequest) {
        QueryWrapper<UserAnswer> queryWrapper = new QueryWrapper<>();
        if (useranswerQueryRequest == null) {
            return queryWrapper;
        }

        Long id = useranswerQueryRequest.getId();
        Long appId = useranswerQueryRequest.getAppId();
        Integer appType = useranswerQueryRequest.getAppType();
        Integer scoringStrategy = useranswerQueryRequest.getScoringStrategy();
        List<String> choices = useranswerQueryRequest.getChoices();
        Long resultId = useranswerQueryRequest.getResultId();
        String resultName = useranswerQueryRequest.getResultName();
        Long userId = useranswerQueryRequest.getUserId();
        String searchText = useranswerQueryRequest.getSearchText();

        // 模糊查詢
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.like("resultName", searchText);
        }

        // 精確查詢
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(appId), "appId", appId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(appType), "appType", appType);
        queryWrapper.eq(ObjectUtils.isNotEmpty(scoringStrategy), "scoringStrategy", scoringStrategy);
        queryWrapper.eq(ObjectUtils.isNotEmpty(resultId), "resultId", resultId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);

        return queryWrapper;
    }

    @Override
    public UserAnswerVO getUserAnswerVO(UserAnswer useranswer, HttpServletRequest request) {
        UserAnswerVO useranswerVO = UserAnswerVO.objToVo(useranswer);
        Long userId = useranswer.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        useranswerVO.setUserVO(userVO);
        return useranswerVO;
    }

    @Override
    public Page<UserAnswerVO> getUserAnswerVOPage(Page<UserAnswer> useranswerPage, HttpServletRequest request) {
        List<UserAnswer> useranswerList = useranswerPage.getRecords();
        Page<UserAnswerVO> useranswerVOPage = new Page<>(useranswerPage.getCurrent(), useranswerPage.getSize(), useranswerPage.getTotal());
        if (CollUtil.isEmpty(useranswerList)) {
            return useranswerVOPage;
        }
        List<UserAnswerVO> useranswerVOList = useranswerList.stream()
                .map(useranswer -> getUserAnswerVO(useranswer, request))
                .collect(Collectors.toList());
        useranswerVOPage.setRecords(useranswerVOList);
        return useranswerVOPage;
    }
}
