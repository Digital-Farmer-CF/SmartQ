package com.cf.smartq.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cf.smartq.common.ErrorCode;
import com.cf.smartq.constant.CommonConstant;
import com.cf.smartq.exception.ThrowUtils;
import com.cf.smartq.mapper.QuestionMapper;
import com.cf.smartq.model.dto.question.QuestionContent;
import com.cf.smartq.model.dto.question.QuestionQueryRequest;
import com.cf.smartq.model.entity.Question;
import com.cf.smartq.model.entity.User;
import com.cf.smartq.model.vo.QuestionVO;
import com.cf.smartq.model.vo.UserVO;
import com.cf.smartq.service.QuestionService;
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
 * 问题服务实现
 *

 */
@Service
@Slf4j
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    @Resource
    private UserService userService;

    /**
     * 校验数据
     *
     * @param question
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validQuestion(Question question, boolean add) {
        ThrowUtils.throwIf(question == null, ErrorCode.PARAMS_ERROR);

        // 校验题目内容不能为空
        String QuestionContent = question.getQuestionContent();
        if (add) {
            ThrowUtils.throwIf(StringUtils.isBlank(QuestionContent), ErrorCode.PARAMS_ERROR, "题目内容不能为空");
        }



        // 校验应用 ID 必须有效
        Long appId = question.getAppId();
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用 ID 无效");

        // 校验用户 ID 必须有效
        Long userId = question.getUserId();
        ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.PARAMS_ERROR, "用户 ID 无效");
    }

    /**
     * 获取查询条件
     *
     * @param questionQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest) {
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        if (questionQueryRequest == null) {
            return queryWrapper;
        }

        // 获取查询条件字段
        Long id = questionQueryRequest.getId();
        Long appId = questionQueryRequest.getAppId();
        Long userId = questionQueryRequest.getUserId();
        Date createTime = questionQueryRequest.getCreateTime();
        Date updateTime = questionQueryRequest.getUpdateTime();
        List<QuestionContent> questionContent = questionQueryRequest.getQuestionContent();
        // 假设这是一个对象，可能需要转换为 JSON 字符串



        // 精确查询
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);  // 等于
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);  // 等于
        queryWrapper.eq(ObjectUtils.isNotEmpty(appId), "appId", appId);  // 应用id（精确查询）

        // 日期范围查询（创建时间和更新时间）
        if (createTime != null) {
            queryWrapper.ge("createTime", createTime);  // 大于等于创建时间
        }
        if (updateTime != null) {
            queryWrapper.le("updateTime", updateTime);  // 小于等于更新时间
        }


        return queryWrapper;
    }

    /**
     * 问题entity转问题vo
     *
     * @param question
     * @param request
     * @return
     */
    @Override
    public QuestionVO getQuestionVO(Question question, HttpServletRequest request) {
        // 对象转封装类
        QuestionVO questionVO = QuestionVO.objToVo(question);
        // 1. 关联查询用户信息
        Long userId = question.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        questionVO.setUserVO(userVO);
        return questionVO;
    }

    /**
     * 分页问题entity转问题vo
     *
     * @param questionPage
     * @param request
     * @return
     */
    @Override
    public Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request) {
        List<Question> questionList = questionPage.getRecords();
        Page<QuestionVO> questionVOPage = new Page<>(questionPage.getCurrent(), questionPage.getSize(), questionPage.getTotal());
        if (CollUtil.isEmpty(questionList)) {
            return questionVOPage;
        }
        // 对象列表 => 封装对象列表
        List<QuestionVO> questionVOList = questionList.stream().map(question -> {
            return QuestionVO.objToVo(question);
        }).collect(Collectors.toList());
        // 1. 关联查询用户信息
        Set<Long> userIdSet = questionList.stream().map(Question::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 填充信息
        questionVOList.forEach(questionVO -> {
            Long userId = questionVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            questionVO.setUserVO(userService.getUserVO(user));
        });
        // endregion

        questionVOPage.setRecords(questionVOList);
        return questionVOPage;
    }

}
