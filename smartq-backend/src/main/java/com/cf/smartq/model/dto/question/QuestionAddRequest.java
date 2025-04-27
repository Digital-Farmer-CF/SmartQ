package com.cf.smartq.model.dto.question;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 创建问题请求
 *

 */
@Data
public class QuestionAddRequest implements Serializable {
    /**
     * 问题的编号
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 题目内容（json格式）
     */
    private List<QuestionContent> questionContent;

    /**
     * 应用 id
     */
    private Long appId;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}