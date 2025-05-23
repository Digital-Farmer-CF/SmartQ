package com.cf.smartq.model.vo;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.cf.smartq.model.dto.question.QuestionContent;
import com.cf.smartq.model.entity.Question;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 问题视图
 *

 */
@Data
public class QuestionVO implements Serializable {
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
         * 应用 id
         */
        private Long appId;

        /**
         * 创建时间
         */
        private Date createTime;

        /**
         * 更新时间
         */
        private Date updateTime;

        /**
         * 展示作者昵称,头像(增加)
         */
        private UserVO userVO;

        /**
         * 应用名称
         */
        private String appName;


    /**
     * 创建用户 id
     */
     private Long userId;

//    /**
//     * 封装类转对象
//     *
//     * @param questionVO
//     * @return
//     */
//    public static Question voToObj(QuestionVO questionVO) {
//        if (questionVO == null) {
//            return null;
//        }
//        Question question = new Question();
//        BeanUtils.copyProperties(questionVO, question);
//        QuestionContent QuestionContent = questionVO.getQuestionContent();
//        QuestionContent.setTags(JSONUtil.toJsonStr(tagList));
//        return question;
//    }

    /**
     * 对象转封装类
     *
     * @param question
     * @return
     */
    public static QuestionVO objToVo(Question question) {
        if (question == null) {
            return null;
        }
        QuestionVO questionVO = new QuestionVO();
        BeanUtils.copyProperties(question, questionVO);
        questionVO.setQuestionContent(JSONUtil.toList(question.getQuestionContent(), QuestionContent.class));
        return questionVO;
    }
}
