package com.cf.smartq.model.dto.question;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 编辑问题请求
 *

 */
@Data
public class QuestionEditRequest implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 题目内容（json格式）
     */
    private List<QuestionContent> questionContent;
    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 应用id
     */
    private Long appId;


}