package com.cf.smartq.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cf.smartq.common.ErrorCode;
import com.cf.smartq.constant.CommonConstant;
import com.cf.smartq.exception.ThrowUtils;
import com.cf.smartq.mapper.AppMapper;
import com.cf.smartq.model.dto.app.AppQueryRequest;
import com.cf.smartq.model.entity.App;
import com.cf.smartq.model.entity.User;
import com.cf.smartq.model.vo.AppVO;
import com.cf.smartq.model.vo.UserVO;
import com.cf.smartq.service.AppService;
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
import java.lang.reflect.Field;
/**
 * 应用服务实现
 *

 */
@Service
@Slf4j
public class AppServiceImpl extends ServiceImpl<AppMapper, App> implements AppService {

    @Resource
    private UserService userService;

    /**
     * 校验数据
     *
     * @param app
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validApp(App app, boolean add) {
        // 判空
        ThrowUtils.throwIf(app == null, ErrorCode.PARAMS_ERROR);

        String appName = app.getAppName();
        String appDesc = app.getAppDesc();
        String appIcon = app.getAppIcon();
        Integer appType = app.getAppType();
        Integer scoringStrategy = app.getScoringStrategy();

        // 创建时必填项校验
        if (add) {
            ThrowUtils.throwIf(StringUtils.isBlank(appName), ErrorCode.PARAMS_ERROR, "应用名不能为空");
            ThrowUtils.throwIf(StringUtils.isBlank(appDesc), ErrorCode.PARAMS_ERROR, "应用描述不能为空");
            ThrowUtils.throwIf(StringUtils.isBlank(appIcon), ErrorCode.PARAMS_ERROR, "应用图标不能为空");
            ThrowUtils.throwIf(appType == null, ErrorCode.PARAMS_ERROR, "应用类型不能为空");
            ThrowUtils.throwIf(scoringStrategy == null, ErrorCode.PARAMS_ERROR, "评分策略不能为空");
        }

        // 公共校验（无论新增或修改）
        if (StringUtils.isNotBlank(appName)) {
            ThrowUtils.throwIf(appName.length() > 50, ErrorCode.PARAMS_ERROR, "应用名不能超过50字符");
        }

        if (StringUtils.isNotBlank(appDesc)) {
            ThrowUtils.throwIf(appDesc.length() > 200, ErrorCode.PARAMS_ERROR, "应用描述不能超过200字符");
        }

        if (StringUtils.isNotBlank(appIcon)) {
            // 简单图标链接格式校验
            boolean isValidUrl = appIcon.startsWith("http://") || appIcon.startsWith("https://");
            ThrowUtils.throwIf(!isValidUrl, ErrorCode.PARAMS_ERROR, "图标地址格式错误");
        }

        if (appType != null) {
            ThrowUtils.throwIf(appType != 0 && appType != 1, ErrorCode.PARAMS_ERROR, "应用类型必须为 0 或 1");
        }

        if (scoringStrategy != null) {
            ThrowUtils.throwIf(scoringStrategy != 0 && scoringStrategy != 1, ErrorCode.PARAMS_ERROR, "评分策略必须为 0 或 1");
        }
    }

    /**
     * 获取查询条件
     *
     * @param appQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<App> getQueryWrapper(AppQueryRequest appQueryRequest) {
        QueryWrapper<App> queryWrapper = new QueryWrapper<>();
        if (appQueryRequest == null) {
            return queryWrapper;
        }

        // 获取所有字段并逐个添加查询条件
        Field[] fields = appQueryRequest.getClass().getDeclaredFields();

        // 遍历字段并添加查询条件
        for (Field field : fields) {
            field.setAccessible(true);  // 使字段可以访问
            try {
                Object fieldValue = field.get(appQueryRequest);  // 获取字段的值

                if (fieldValue != null && !"".equals(fieldValue)) {
                    String fieldName = field.getName();  // 获取字段名

                    // 根据字段类型设置查询条件
                    if (fieldValue instanceof String) {
                        queryWrapper.like(fieldName, fieldValue);  // 字符串字段用模糊查询
                    } else if (fieldValue instanceof Long || fieldValue instanceof Integer) {
                        queryWrapper.eq(fieldName, fieldValue);  // Long 和 Integer 类型字段用精确查询
                    } else if (fieldValue instanceof List) {
                        // 对于 List 类型字段，使用 "in" 查询
                        List<?> list = (List<?>) fieldValue;
                        if (!list.isEmpty()) {
                            queryWrapper.in(fieldName, list);
                        }
                    } else if (fieldValue instanceof Object) {
                        queryWrapper.eq(fieldName, fieldValue);  // 对于其他对象类型使用精确查询
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        // 从多字段中搜索 (搜索标题和内容)
        if (StringUtils.isNotBlank(appQueryRequest.getSearchText())) {
            queryWrapper.and(qw -> qw.like("title", appQueryRequest.getSearchText())
                    .or().like("content", appQueryRequest.getSearchText()));
        }

        // 排除某个 id
        if (ObjectUtils.isNotEmpty(appQueryRequest.getNotId())) {
            queryWrapper.ne("id", appQueryRequest.getNotId());
        }

        // 排序规则
        String sortField = appQueryRequest.getSortField();
        String sortOrder = appQueryRequest.getSortOrder();
        if (StringUtils.isNotBlank(sortField)) {
            queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                    sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                    sortField);
        }

        return queryWrapper;
    }

    /**
     * 获取应用封装
     *
     * @param app
     * @param request
     * @return
     */
    @Override
    public AppVO getAppVO(App app, HttpServletRequest request) {
        // 对象转封装类
        AppVO appVO = AppVO.objToVo(app);
        // 1. 关联查询用户信息
        Long userId = app.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        appVO.setUser(userVO);
        return appVO;
    }

    /**
     * 分页获取应用封装
     *
     * @param appPage
     * @param request
     * @return
     */
    @Override
    public Page<AppVO> getAppVOPage(Page<App> appPage, HttpServletRequest request) {
        List<App> appList = appPage.getRecords();
        Page<AppVO> appVOPage = new Page<>(appPage.getCurrent(), appPage.getSize(), appPage.getTotal());
        if (CollUtil.isEmpty(appList)) {
            return appVOPage;
        }
        // 对象列表 => 封装对象列表
        List<AppVO> appVOList = appList.stream().map(app -> {
            return AppVO.objToVo(app);
        }).collect(Collectors.toList());
        // 1. 关联查询用户信息
        Set<Long> userIdSet = appList.stream().map(App::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        //填充信息
        appVOList.forEach(appVO -> {
            Long userId = appVO.getUserId();
            User user = null;
            if(userIdUserListMap.containsKey(userId)){
                user= userIdUserListMap.get(userId).get(0);
            }
            appVO.setUser(userService.getUserVO(user));
                });

        appVOPage.setRecords(appVOList);
        return appVOPage;
    }
}
