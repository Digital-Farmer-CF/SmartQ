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
import com.cf.smartq.model.entity.UserAnswerFavour;
import com.cf.smartq.model.entity.UserAnswerThumb;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
        // todo 从对象中取值
        String title = useranswer.getTitle();
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
     * @param useranswerQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<UserAnswer> getQueryWrapper(UserAnswerQueryRequest useranswerQueryRequest) {
        QueryWrapper<UserAnswer> queryWrapper = new QueryWrapper<>();
        if (useranswerQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long id = useranswerQueryRequest.getId();
        Long notId = useranswerQueryRequest.getNotId();
        String title = useranswerQueryRequest.getTitle();
        String content = useranswerQueryRequest.getContent();
        String searchText = useranswerQueryRequest.getSearchText();
        String sortField = useranswerQueryRequest.getSortField();
        String sortOrder = useranswerQueryRequest.getSortOrder();
        List<String> tagList = useranswerQueryRequest.getTags();
        Long userId = useranswerQueryRequest.getUserId();
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

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Long userId = useranswer.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        useranswerVO.setUser(userVO);
        // 2. 已登录，获取用户点赞、收藏状态
        long useranswerId = useranswer.getId();
        User loginUser = userService.getLoginUserPermitNull(request);
        if (loginUser != null) {
            // 获取点赞
            QueryWrapper<UserAnswerThumb> useranswerThumbQueryWrapper = new QueryWrapper<>();
            useranswerThumbQueryWrapper.in("useranswerId", useranswerId);
            useranswerThumbQueryWrapper.eq("userId", loginUser.getId());
            UserAnswerThumb useranswerThumb = useranswerThumbMapper.selectOne(useranswerThumbQueryWrapper);
            useranswerVO.setHasThumb(useranswerThumb != null);
            // 获取收藏
            QueryWrapper<UserAnswerFavour> useranswerFavourQueryWrapper = new QueryWrapper<>();
            useranswerFavourQueryWrapper.in("useranswerId", useranswerId);
            useranswerFavourQueryWrapper.eq("userId", loginUser.getId());
            UserAnswerFavour useranswerFavour = useranswerFavourMapper.selectOne(useranswerFavourQueryWrapper);
            useranswerVO.setHasFavour(useranswerFavour != null);
        }
        // endregion

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

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Set<Long> userIdSet = useranswerList.stream().map(UserAnswer::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 2. 已登录，获取用户点赞、收藏状态
        Map<Long, Boolean> useranswerIdHasThumbMap = new HashMap<>();
        Map<Long, Boolean> useranswerIdHasFavourMap = new HashMap<>();
        User loginUser = userService.getLoginUserPermitNull(request);
        if (loginUser != null) {
            Set<Long> useranswerIdSet = useranswerList.stream().map(UserAnswer::getId).collect(Collectors.toSet());
            loginUser = userService.getLoginUser(request);
            // 获取点赞
            QueryWrapper<UserAnswerThumb> useranswerThumbQueryWrapper = new QueryWrapper<>();
            useranswerThumbQueryWrapper.in("useranswerId", useranswerIdSet);
            useranswerThumbQueryWrapper.eq("userId", loginUser.getId());
            List<UserAnswerThumb> useranswerUserAnswerThumbList = useranswerThumbMapper.selectList(useranswerThumbQueryWrapper);
            useranswerUserAnswerThumbList.forEach(useranswerUserAnswerThumb -> useranswerIdHasThumbMap.put(useranswerUserAnswerThumb.getUserAnswerId(), true));
            // 获取收藏
            QueryWrapper<UserAnswerFavour> useranswerFavourQueryWrapper = new QueryWrapper<>();
            useranswerFavourQueryWrapper.in("useranswerId", useranswerIdSet);
            useranswerFavourQueryWrapper.eq("userId", loginUser.getId());
            List<UserAnswerFavour> useranswerFavourList = useranswerFavourMapper.selectList(useranswerFavourQueryWrapper);
            useranswerFavourList.forEach(useranswerFavour -> useranswerIdHasFavourMap.put(useranswerFavour.getUserAnswerId(), true));
        }
        // 填充信息
        useranswerVOList.forEach(useranswerVO -> {
            Long userId = useranswerVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            useranswerVO.setUser(userService.getUserVO(user));
            useranswerVO.setHasThumb(useranswerIdHasThumbMap.getOrDefault(useranswerVO.getId(), false));
            useranswerVO.setHasFavour(useranswerIdHasFavourMap.getOrDefault(useranswerVO.getId(), false));
        });
        // endregion

        useranswerVOPage.setRecords(useranswerVOList);
        return useranswerVOPage;
    }

}
