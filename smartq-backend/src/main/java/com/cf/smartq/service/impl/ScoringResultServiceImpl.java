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
 * 评分结果服务实现
 *

 */
@Service
@Slf4j
public class ScoringResultServiceImpl extends ServiceImpl<ScoringResultMapper, ScoringResult> implements ScoringResultService {

    @Resource
    private UserService userService;

    /**
     * 校验数据
     *
     * @param scoringresult
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validScoringResult(ScoringResult scoringresult, boolean add) {
        ThrowUtils.throwIf(scoringresult == null, ErrorCode.PARAMS_ERROR);
        // 从对象中取值
        Long appId = scoringresult.getAppId();
        Date createTime = scoringresult.getCreateTime();
        String resultProp = scoringresult.getResultProp();
        Integer resultScoreRange = scoringresult.getResultScoreRange();
        Date updateTime = scoringresult.getUpdateTime();
        Long userId = scoringresult.getUserId();

        // 创建时必填项校验
        if (add) {
            ThrowUtils.throwIf(StringUtils.isBlank(String.valueOf(appId)), ErrorCode.PARAMS_ERROR, "应用名不能为空");
            ThrowUtils.throwIf(StringUtils.isBlank(resultProp), ErrorCode.PARAMS_ERROR, "评分结果不能为空");
            ThrowUtils.throwIf(StringUtils.isBlank(String.valueOf(userId)), ErrorCode.PARAMS_ERROR, "用户id不能为空");
            ThrowUtils.throwIf(StringUtils.isBlank(String.valueOf(resultScoreRange)), ErrorCode.PARAMS_ERROR, "评分结果不能为空");
        }

        // 公共校验（无论新增或修改）
        // 校验 resultProp 的长度
        if (StringUtils.isNotBlank(resultProp)) {
            ThrowUtils.throwIf(resultProp.length() > 200, ErrorCode.PARAMS_ERROR, "评分结果长度不能超过200字符");
        }

        // 校验 resultScoreRange 是否在合理范围内
        if (resultScoreRange != null) {
            ThrowUtils.throwIf(resultScoreRange < 0 || resultScoreRange > 100, ErrorCode.PARAMS_ERROR, "评分结果范围必须在 0 到 100 之间");
        }

        // 校验创建时间和更新时间是否为空（假设它们不应该为空）
        if (createTime == null) {
            ThrowUtils.throwIf(true, ErrorCode.PARAMS_ERROR, "创建时间不能为空");
        }
        if (updateTime == null) {
            ThrowUtils.throwIf(true, ErrorCode.PARAMS_ERROR, "更新时间不能为空");
        }

        // 校验时间的合理性（假设更新时间不能早于创建时间）
        if (updateTime != null && createTime != null) {
            ThrowUtils.throwIf(updateTime.before(createTime), ErrorCode.PARAMS_ERROR, "更新时间不能早于创建时间");
        }
    }

    /**
     * 获取查询条件
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
        // todo 从对象中取值
        Long id = scoringresultQueryRequest.getId();
        String resultName = scoringresultQueryRequest.getResultName();
        String resultDesc = scoringresultQueryRequest.getResultDesc();
        Integer resultScoreRange = scoringresultQueryRequest.getResultScoreRange();
        Long appId = scoringresultQueryRequest.getAppId();
        Long userId = scoringresultQueryRequest.getUserId();
        String searchText = scoringresultQueryRequest.getSearchText();
        // todo 补充需要的查询条件
        // 从多字段中搜索（例如搜索标题和内容）
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.and(qw -> qw.like("resultName", searchText).or().like("resultDesc", searchText));
        }

        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(resultName), "resultName", resultName);
        queryWrapper.like(StringUtils.isNotBlank(resultDesc), "resultDesc", resultDesc);
        
        

        // 精确查询，检查传入的字段值是否为空
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);  // 精确匹配 id
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);  // 精确匹配 userId
        queryWrapper.eq(ObjectUtils.isNotEmpty(appId), "appId", appId);  // 精确匹配 appId
        // 评分结果范围的查询
        if (resultScoreRange != null) {
            queryWrapper.eq("resultScoreRange", resultScoreRange);
        }

        return queryWrapper;
    }

    /**
     * 获取评分结果封装
     *
     * @param scoringresult
     * @param request
     * @return
     */
    @Override
    public ScoringResultVO getScoringResultVO(ScoringResult scoringresult, HttpServletRequest request) {
        // 对象转封装类
        ScoringResultVO scoringresultVO = ScoringResultVO.objToVo(scoringresult);
        // 1. 关联查询用户信息
        Long userId = scoringresult.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        scoringresultVO.setUserVO(userVO);
        return scoringresultVO;
    }

    /**
     * 分页获取评分结果封装
     *
     * @param scoringresultPage
     * @param request
     * @return
     */
    @Override
    public Page<ScoringResultVO> getScoringResultVOPage(Page<ScoringResult> scoringresultPage, HttpServletRequest request) {
        List<ScoringResult> scoringresultList = scoringresultPage.getRecords();
        Page<ScoringResultVO> scoringresultVOPage = new Page<>(scoringresultPage.getCurrent(), scoringresultPage.getSize(), scoringresultPage.getTotal());
        if (CollUtil.isEmpty(scoringresultList)) {
            return scoringresultVOPage;
        }
        // 对象列表 => 封装对象列表
        List<ScoringResultVO> scoringresultVOList = scoringresultList.stream().map(scoringresult -> {
            return ScoringResultVO.objToVo(scoringresult);
        }).collect(Collectors.toList());
        // 1. 关联查询用户信息
        Set<Long> userIdSet = scoringresultList.stream().map(ScoringResult::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));

        // 填充信息
        scoringresultVOList.forEach(scoringresultVO -> {
            Long userId = scoringresultVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            scoringresultVO.setUserVO(userService.getUserVO(user));
        });

        scoringresultVOPage.setRecords(scoringresultVOList);
        return scoringresultVOPage;
    }

}
