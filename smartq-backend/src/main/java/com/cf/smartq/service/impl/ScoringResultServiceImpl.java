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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
        // todo 从对象中取值
        String title = scoringresult.getTitle();
        // 创建数据时，参数不能为空
        if (add) {
            // todo 补充校验规则
            ThrowUtils.throwIf(StringUtils.isBlank(title), ErrorCode.PARAMS_ERROR);
        }
        // 修改数据时，有参数则校验
        // todo 补充校验规则
        if (StringUtils.isNotBlank(title)) {
            ThrowUtils.throwIf(title.length() > 80, ErrorCode.PARAMS_ERROR, "标题过长");
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
        Long notId = scoringresultQueryRequest.getNotId();
        String title = scoringresultQueryRequest.getTitle();
        String content = scoringresultQueryRequest.getContent();
        String searchText = scoringresultQueryRequest.getSearchText();
        String sortField = scoringresultQueryRequest.getSortField();
        String sortOrder = scoringresultQueryRequest.getSortOrder();
        List<String> tagList = scoringresultQueryRequest.getTags();
        Long userId = scoringresultQueryRequest.getUserId();
        // todo 补充需要的查询条件
        // 从多字段中搜索
        if (StringUtils.isNotBlank(searchText)) {
            // 需要拼接查询条件
            queryWrapper.and(qw -> qw.like("title", searchText).or().like("content", searchText));
        }
        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        // JSON 数组查询
        if (CollUtil.isNotEmpty(tagList)) {
            for (String tag : tagList) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        // 精确查询
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
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

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Long userId = scoringresult.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        scoringresultVO.setUser(userVO);
        // 2. 已登录，获取用户点赞、收藏状态
        long scoringresultId = scoringresult.getId();
        User loginUser = userService.getLoginUserPermitNull(request);
        if (loginUser != null) {
            // 获取点赞
            QueryWrapper<ScoringResultThumb> scoringresultThumbQueryWrapper = new QueryWrapper<>();
            scoringresultThumbQueryWrapper.in("scoringresultId", scoringresultId);
            scoringresultThumbQueryWrapper.eq("userId", loginUser.getId());
            ScoringResultThumb scoringresultThumb = scoringresultThumbMapper.selectOne(scoringresultThumbQueryWrapper);
            scoringresultVO.setHasThumb(scoringresultThumb != null);
            // 获取收藏
            QueryWrapper<ScoringResultFavour> scoringresultFavourQueryWrapper = new QueryWrapper<>();
            scoringresultFavourQueryWrapper.in("scoringresultId", scoringresultId);
            scoringresultFavourQueryWrapper.eq("userId", loginUser.getId());
            ScoringResultFavour scoringresultFavour = scoringresultFavourMapper.selectOne(scoringresultFavourQueryWrapper);
            scoringresultVO.setHasFavour(scoringresultFavour != null);
        }
        // endregion

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

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Set<Long> userIdSet = scoringresultList.stream().map(ScoringResult::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 2. 已登录，获取用户点赞、收藏状态
        Map<Long, Boolean> scoringresultIdHasThumbMap = new HashMap<>();
        Map<Long, Boolean> scoringresultIdHasFavourMap = new HashMap<>();
        User loginUser = userService.getLoginUserPermitNull(request);
        if (loginUser != null) {
            Set<Long> scoringresultIdSet = scoringresultList.stream().map(ScoringResult::getId).collect(Collectors.toSet());
            loginUser = userService.getLoginUser(request);
            // 获取点赞
            QueryWrapper<ScoringResultThumb> scoringresultThumbQueryWrapper = new QueryWrapper<>();
            scoringresultThumbQueryWrapper.in("scoringresultId", scoringresultIdSet);
            scoringresultThumbQueryWrapper.eq("userId", loginUser.getId());
            List<ScoringResultThumb> scoringresultScoringResultThumbList = scoringresultThumbMapper.selectList(scoringresultThumbQueryWrapper);
            scoringresultScoringResultThumbList.forEach(scoringresultScoringResultThumb -> scoringresultIdHasThumbMap.put(scoringresultScoringResultThumb.getScoringResultId(), true));
            // 获取收藏
            QueryWrapper<ScoringResultFavour> scoringresultFavourQueryWrapper = new QueryWrapper<>();
            scoringresultFavourQueryWrapper.in("scoringresultId", scoringresultIdSet);
            scoringresultFavourQueryWrapper.eq("userId", loginUser.getId());
            List<ScoringResultFavour> scoringresultFavourList = scoringresultFavourMapper.selectList(scoringresultFavourQueryWrapper);
            scoringresultFavourList.forEach(scoringresultFavour -> scoringresultIdHasFavourMap.put(scoringresultFavour.getScoringResultId(), true));
        }
        // 填充信息
        scoringresultVOList.forEach(scoringresultVO -> {
            Long userId = scoringresultVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            scoringresultVO.setUser(userService.getUserVO(user));
            scoringresultVO.setHasThumb(scoringresultIdHasThumbMap.getOrDefault(scoringresultVO.getId(), false));
            scoringresultVO.setHasFavour(scoringresultIdHasFavourMap.getOrDefault(scoringresultVO.getId(), false));
        });
        // endregion

        scoringresultVOPage.setRecords(scoringresultVOList);
        return scoringresultVOPage;
    }

}
