package com.cf.smartq.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cf.smartq.annotation.AuthCheck;
import com.cf.smartq.common.BaseResponse;
import com.cf.smartq.common.DeleteRequest;
import com.cf.smartq.common.ErrorCode;
import com.cf.smartq.common.ResultUtils;
import com.cf.smartq.constant.UserConstant;
import com.cf.smartq.exception.BusinessException;
import com.cf.smartq.exception.ThrowUtils;
import com.cf.smartq.model.dto.useranswer.UserAnswerAddRequest;
import com.cf.smartq.model.dto.useranswer.UserAnswerEditRequest;
import com.cf.smartq.model.dto.useranswer.UserAnswerQueryRequest;
import com.cf.smartq.model.dto.useranswer.UserAnswerUpdateRequest;
import com.cf.smartq.model.entity.UserAnswer;
import com.cf.smartq.model.entity.User;
import com.cf.smartq.model.vo.UserAnswerVO;
import com.cf.smartq.service.UserAnswerService;
import com.cf.smartq.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 用户回答接口
 *
 */
@RestController
@RequestMapping("/useranswer")
@Slf4j
public class UserAnswerController {

    @Resource
    private UserAnswerService useranswerService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建用户回答
     *
     * @param useranswerAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addUserAnswer(@RequestBody UserAnswerAddRequest useranswerAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(useranswerAddRequest == null, ErrorCode.PARAMS_ERROR);
        // todo 在此处将实体类和 DTO 进行转换
        UserAnswer useranswer = new UserAnswer();
        BeanUtils.copyProperties(useranswerAddRequest, useranswer);
        // 数据校验
        useranswerService.validUserAnswer(useranswer, true);
        // todo 填充默认值
        User loginUser = userService.getLoginUser(request);
        useranswer.setUserId(loginUser.getId());
        // 写入数据库
        boolean result = useranswerService.save(useranswer);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newUserAnswerId = useranswer.getId();
        return ResultUtils.success(newUserAnswerId);
    }

    /**
     * 删除用户回答
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUserAnswer(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        UserAnswer oldUserAnswer = useranswerService.getById(id);
        ThrowUtils.throwIf(oldUserAnswer == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldUserAnswer.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = useranswerService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新用户回答（仅管理员可用）
     *
     * @param useranswerUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUserAnswer(@RequestBody UserAnswerUpdateRequest useranswerUpdateRequest) {
        if (useranswerUpdateRequest == null || useranswerUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        UserAnswer useranswer = new UserAnswer();
        BeanUtils.copyProperties(useranswerUpdateRequest, useranswer);
        // 数据校验
        useranswerService.validUserAnswer(useranswer, false);
        // 判断是否存在
        long id = useranswerUpdateRequest.getId();
        UserAnswer oldUserAnswer = useranswerService.getById(id);
        ThrowUtils.throwIf(oldUserAnswer == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = useranswerService.updateById(useranswer);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取用户回答（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<UserAnswerVO> getUserAnswerVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        UserAnswer useranswer = useranswerService.getById(id);
        ThrowUtils.throwIf(useranswer == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(useranswerService.getUserAnswerVO(useranswer, request));
    }

    /**
     * 分页获取用户回答列表（仅管理员可用）
     *
     * @param useranswerQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserAnswer>> listUserAnswerByPage(@RequestBody UserAnswerQueryRequest useranswerQueryRequest) {
        long current = useranswerQueryRequest.getCurrent();
        long size = useranswerQueryRequest.getPageSize();
        // 查询数据库
        Page<UserAnswer> useranswerPage = useranswerService.page(new Page<>(current, size),
                useranswerService.getQueryWrapper(useranswerQueryRequest));
        return ResultUtils.success(useranswerPage);
    }

    /**
     * 分页获取用户回答列表（封装类）
     *
     * @param useranswerQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<UserAnswerVO>> listUserAnswerVOByPage(@RequestBody UserAnswerQueryRequest useranswerQueryRequest,
                                                               HttpServletRequest request) {
        long current = useranswerQueryRequest.getCurrent();
        long size = useranswerQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<UserAnswer> useranswerPage = useranswerService.page(new Page<>(current, size),
                useranswerService.getQueryWrapper(useranswerQueryRequest));
        // 获取封装类
        return ResultUtils.success(useranswerService.getUserAnswerVOPage(useranswerPage, request));
    }

    /**
     * 分页获取当前登录用户创建的用户回答列表
     *
     * @param useranswerQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<UserAnswerVO>> listMyUserAnswerVOByPage(@RequestBody UserAnswerQueryRequest useranswerQueryRequest,
                                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(useranswerQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        User loginUser = userService.getLoginUser(request);
        useranswerQueryRequest.setUserId(loginUser.getId());
        long current = useranswerQueryRequest.getCurrent();
        long size = useranswerQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<UserAnswer> useranswerPage = useranswerService.page(new Page<>(current, size),
                useranswerService.getQueryWrapper(useranswerQueryRequest));
        // 获取封装类
        return ResultUtils.success(useranswerService.getUserAnswerVOPage(useranswerPage, request));
    }

    /**
     * 编辑用户回答（给用户使用）
     *
     * @param useranswerEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editUserAnswer(@RequestBody UserAnswerEditRequest useranswerEditRequest, HttpServletRequest request) {
        if (useranswerEditRequest == null || useranswerEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        UserAnswer useranswer = new UserAnswer();
        BeanUtils.copyProperties(useranswerEditRequest, useranswer);
        // 数据校验
        useranswerService.validUserAnswer(useranswer, false);
        User loginUser = userService.getLoginUser(request);
        // 判断是否存在
        long id = useranswerEditRequest.getId();
        UserAnswer oldUserAnswer = useranswerService.getById(id);
        ThrowUtils.throwIf(oldUserAnswer == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldUserAnswer.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = useranswerService.updateById(useranswer);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    // endregion
}
