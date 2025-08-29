package com.cf.smartq.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cf.smartq.model.dto.useranswer.UserAnswerQueryRequest;
import com.cf.smartq.model.entity.UserAnswer;
import com.cf.smartq.model.vo.UserAnswerVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户回答服务
 *

 */
public interface UserAnswerService extends IService<UserAnswer> {

    /**
     * 校验数据
     *
     * @param useranswer
     * @param add 对创建的数据进行校验
     */
    void validUserAnswer(UserAnswer useranswer, boolean add);

    /**
     * 获取查询条件
     *
     * @param useranswerQueryRequest
     * @return
     */
    QueryWrapper<UserAnswer> getQueryWrapper(UserAnswerQueryRequest useranswerQueryRequest);
    
    /**
     * 获取用户回答封装
     *
     * @param useranswer
     * @param request
     * @return
     */
    UserAnswerVO getUserAnswerVO(UserAnswer useranswer, HttpServletRequest request);

    /**
     * 分页获取用户回答封装
     *
     * @param useranswerPage
     * @param request
     * @return
     */
    Page<UserAnswerVO> getUserAnswerVOPage(Page<UserAnswer> useranswerPage, HttpServletRequest request);
}
