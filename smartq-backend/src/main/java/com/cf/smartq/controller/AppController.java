package com.cf.smartq.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cf.smartq.annotation.AuthCheck;
import com.cf.smartq.common.*;
import com.cf.smartq.constant.UserConstant;
import com.cf.smartq.exception.BusinessException;
import com.cf.smartq.exception.ThrowUtils;
import com.cf.smartq.model.dto.app.AppAddRequest;
import com.cf.smartq.model.dto.app.AppEditRequest;
import com.cf.smartq.model.dto.app.AppQueryRequest;
import com.cf.smartq.model.dto.app.AppUpdateRequest;
import com.cf.smartq.model.entity.App;
import com.cf.smartq.model.entity.User;
import com.cf.smartq.model.enums.AppReviewStatusEnum;
import com.cf.smartq.model.vo.AppVO;
import com.cf.smartq.service.AppService;
import com.cf.smartq.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Optional;

/**
 * 应用接口
 *
 */
@RestController
@RequestMapping("/app")
@Slf4j
@ApiOperation(value = "应用接口")
public class AppController {

    @Resource
    private AppService appService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建应用
     *
     * @param appAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @ApiOperation(value = "创建应用")
    public BaseResponse<Long> addApp(@RequestBody AppAddRequest appAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(appAddRequest == null, ErrorCode.PARAMS_ERROR);
        // 将实体类和 DTO 进行转换
        App app = new App();
        BeanUtils.copyProperties(appAddRequest, app);
        // 数据校验
        appService.validApp(app, true);
        // 获取当前登录用户,把id填入app
        User loginUser = userService.getLoginUser(request);
        app.setUserId(loginUser.getId());
        // 写入数据库
        boolean result = appService.save(app);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newAppId = app.getId();
        return ResultUtils.success(newAppId);
    }

    /**
     * 删除应用
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @ApiOperation(value = "删除应用")
    public BaseResponse<Boolean> deleteApp(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        App oldApp = appService.getById(id);
        ThrowUtils.throwIf(oldApp == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldApp.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = appService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新应用（仅管理员可用）
     *
     * @param appUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "更新应用(管理员)")
    public BaseResponse<Boolean> updateApp(@RequestBody AppUpdateRequest appUpdateRequest) {
        if (appUpdateRequest == null || appUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 在此处将实体类和 DTO 进行转换
        App app = new App();
        BeanUtils.copyProperties(appUpdateRequest, app);
        // 数据校验
        appService.validApp(app, false);
        // 判断是否存在
        long id = appUpdateRequest.getId();
        App oldApp = appService.getById(id);
        ThrowUtils.throwIf(oldApp == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = appService.updateById(app);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取应用（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    @ApiOperation(value = "根据id查询应用")
    public BaseResponse<AppVO> getAppVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        App app = appService.getById(id);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(appService.getAppVO(app, request));
    }

    /**
     * 分页获取应用列表（仅管理员可用）
     *
     * @param appQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "(管理员)分页查询应用列表")
    public BaseResponse<Page<App>> listAppByPage(@RequestBody AppQueryRequest appQueryRequest) {
        long current = appQueryRequest.getCurrent();
        long size = appQueryRequest.getPageSize();
        // 查询数据库
        Page<App> appPage = appService.page(new Page<>(current, size),
                appService.getQueryWrapper(appQueryRequest));
        return ResultUtils.success(appPage);
    }

    /**
     * 分页获取应用列表（封装类）
     *
     * @param appQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    @ApiOperation(value = "分页查询应用")
    public BaseResponse<Page<AppVO>> listAppVOByPage(@RequestBody AppQueryRequest appQueryRequest,
                                                               HttpServletRequest request) {
        long current = appQueryRequest.getCurrent();
        long size = appQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Long appId = appQueryRequest.getId();
      // 只有当用户提供了应用ID时，才检查该应用是否审核通过
        if (appId != null) {
            App app = appService.getById(appId);
            String reviewStatus = Optional.ofNullable(app)
                    .map(App::getReviewStatus)
                    .map(Object::toString)
                    .orElse("");
            if(!AppReviewStatusEnum.Pass.equals(AppReviewStatusEnum.getEnumByValue(reviewStatus))){
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "该应用尚未通过审核");
            }
        }
       // 继续查询数据库...

        // 查询数据库
        Page<App> appPage = appService.page(new Page<>(current, size),
                appService.getQueryWrapper(appQueryRequest));
        // 获取封装类
        return ResultUtils.success(appService.getAppVOPage(appPage, request));
    }

    /**
     * 分页获取当前登录用户创建的应用列表
     *
     * @param appQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    @ApiOperation(value = "分页查询当前登录用户的应用")
    public BaseResponse<Page<AppVO>> listMyAppVOByPage(@RequestBody AppQueryRequest appQueryRequest,
                                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(appQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        User loginUser = userService.getLoginUser(request);
        appQueryRequest.setUserId(loginUser.getId());
        long current = appQueryRequest.getCurrent();
        long size = appQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<App> appPage = appService.page(new Page<>(current, size),
                appService.getQueryWrapper(appQueryRequest));
        // 获取封装类
        return ResultUtils.success(appService.getAppVOPage(appPage, request));
    }

    /**
     * 编辑应用（给用户使用）
     *
     * @param appEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    @ApiOperation(value = "编辑应用")
    public BaseResponse<Boolean> editApp(@RequestBody AppEditRequest appEditRequest, HttpServletRequest request) {
        if (appEditRequest == null || appEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 在此处将实体类和 DTO 进行转换
        App app = new App();
        BeanUtils.copyProperties(appEditRequest, app);
        // 数据校验
        appService.validApp(app, false);
        User loginUser = userService.getLoginUser(request);
        // 判断是否存在
        long id = appEditRequest.getId();
        App oldApp = appService.getById(id);
        ThrowUtils.throwIf(oldApp == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldApp.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = appService.updateById(app);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 应用审核接口
     * @param reviewRequest 包含审核信息的请求体（应用ID和审核状态）
     * @param request HTTP请求对象，用于获取当前登录用户
     * @return 返回操作是否成功
     */
    @PostMapping("/review")  // 定义这是一个POST请求，路径为/review
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)  // 权限校验：必须是管理员角色才能访问
    @ApiOperation(value = "审核应用")
    public BaseResponse<Boolean> doAppReview(@RequestBody ReviewRequest reviewRequest, HttpServletRequest request) {

        // 1. 基本参数校验
        // 检查请求体是否为null，如果是则抛出参数错误异常
        ThrowUtils.throwIf(reviewRequest == null, ErrorCode.PARAMS_ERROR);

        // 从请求体中提取应用ID和审核状态
        Long id = reviewRequest.getId();
        Integer reviewStatus = reviewRequest.getReviewStatus();

        // 2. 业务参数校验
        // 通过枚举类验证审核状态是否有效
        AppReviewStatusEnum reviewStatusEnum = AppReviewStatusEnum.getEnumByValue(String.valueOf(reviewStatus));

        // 检查应用ID和审核状态是否有效
        if (id == null || reviewStatusEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 3. 业务逻辑校验
        // 查询要审核的应用是否存在
        App oldApp = appService.getById(id);
        // 如果应用不存在，抛出未找到异常
        ThrowUtils.throwIf(oldApp == null, ErrorCode.NOT_FOUND_ERROR);

        // 检查是否已经是目标审核状态（避免重复审核）
        if (oldApp.getReviewStatus().equals(reviewStatus)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请勿重复审核");
        }

        // 4. 构建更新对象
        // 获取当前登录用户（审核人）
        User loginUser = userService.getLoginUser(request);

        // 创建要更新的应用对象
        App app = new App();
        app.setId(id);  // 设置应用ID
        app.setReviewStatus(reviewStatus);  // 设置新的审核状态
        app.setReviewerId(loginUser.getId());  // 设置审核人ID
        app.setReviewTime(new Date());  // 设置审核时间为当前时间

        // 5. 执行更新操作
        boolean result = appService.updateById(app);
        // 如果更新失败，抛出操作异常
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        // 6. 返回成功响应
        return ResultUtils.success(true);
    }

}
