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
 * 用户回答服务实现
 *

 */
@Service
@Slf4j
public class UserAnswerServiceImpl extends ServiceImpl<UserAnswerMapper, UserAnswer> implements UserAnswerService {

    @Resource
    private UserService userService;

    /**
     * 校验数据
     *
     * @param useranswer
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validUserAnswer(UserAnswer useranswer, boolean add) {
        ThrowUtils.throwIf(useranswer == null, ErrorCode.PARAMS_ERROR);
        // 从对象中取值
        Long appId = useranswer.getAppId();
        Integer appType = useranswer.getAppType();
        Integer scoringStrategy = useranswer.getScoringStrategy();
        String choices = useranswer.getChoices();
        Long resultId = useranswer.getResultId();
        String resultName = useranswer.getResultName();
        Date createTime = useranswer.getCreateTime();
        Date updateTime = useranswer.getUpdateTime();
        // 创建数据时，参数不能为空
        if (add) {
            // 补充校验规则
            String appId1 = String.valueOf(appId);
            ThrowUtils.throwIf(StringUtils.isBlank(appId1), ErrorCode.PARAMS_ERROR);
            ThrowUtils.throwIf(StringUtils.isBlank(choices), ErrorCode.PARAMS_ERROR);
        }
        // 修改数据时，有参数则校验
        // 充校验规则
        if (StringUtils.isNotBlank(resultName)) {
            ThrowUtils.throwIf(resultName.length() > 80, ErrorCode.PARAMS_ERROR, "结果名称过长");
        }
    }

    /**
     * 获取查询条件
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
        // 从对象中取值
        Long id = useranswerQueryRequest.getId();
        Integer appType = useranswerQueryRequest.getAppType();
        Integer scoringStrategy = useranswerQueryRequest.getScoringStrategy();
        Integer resultScore = useranswerQueryRequest.getResultScore();
        Long userId = useranswerQueryRequest.getUserId();
        // 补充需要的查询条件
        // 精确查询
        queryWrapper.ne(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(appType), "appType", appType);
        queryWrapper.eq(ObjectUtils.isNotEmpty(scoringStrategy), "scoringStrategy", scoringStrategy);
        queryWrapper.eq(ObjectUtils.isNotEmpty(resultScore), "resultScore", resultScore);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        return queryWrapper;
    }

    /**
     * 获取用户回答封装
     *
     * @param useranswer
     * @param request
     * @return
     */
    @Override
    public UserAnswerVO getUserAnswerVO(UserAnswer useranswer, HttpServletRequest request) {
        // 对象转封装类
        UserAnswerVO useranswerVO = UserAnswerVO.objToVo(useranswer);
        // 1. 关联查询用户信息
        Long userId = useranswer.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        useranswerVO.setUserVO(userVO);
        // 2. 已登录，获取用户点赞、收藏状态

        return useranswerVO;
    }

    /**
     * 分页获取用户回答封装
     *
     * @param useranswerPage
     * @param request
     * @return
     */
    @Override
    public Page<UserAnswerVO> getUserAnswerVOPage(Page<UserAnswer> useranswerPage, HttpServletRequest request) {
        List<UserAnswer> useranswerList = useranswerPage.getRecords();
        Page<UserAnswerVO> useranswerVOPage = new Page<>(useranswerPage.getCurrent(), useranswerPage.getSize(), useranswerPage.getTotal());
        if (CollUtil.isEmpty(useranswerList)) {
            return useranswerVOPage;
        }
        // 对象列表 => 封装对象列表
        List<UserAnswerVO> useranswerVOList = useranswerList.stream().map(useranswer -> {
            return UserAnswerVO.objToVo(useranswer);
        }).collect(Collectors.toList());
        // 1. 关联查询用户信息
        Set<Long> userIdSet = useranswerList.stream().map(UserAnswer::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 填充信息
        useranswerVOList.forEach(useranswerVO -> {
            Long userId = useranswerVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            useranswerVO.setUserVO(userService.getUserVO(user));
        });
        // endregion

        useranswerVOPage.setRecords(useranswerVOList);
        return useranswerVOPage;
    }

}
