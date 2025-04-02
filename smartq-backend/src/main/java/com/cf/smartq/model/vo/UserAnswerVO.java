package com.cf.smartq.model.vo;

import cn.hutool.json.JSONUtil;
import com.cf.smartq.model.entity.UserAnswer;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 用户回答视图
 *

 */
@Data
public class UserAnswerVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

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

    /**
     * 标签列表
     */
    private List<String> tagList;

    /**
     * 创建用户信息
     */
    private UserVO user;

    /**
     * 封装类转对象
     *
     * @param useranswerVO
     * @return
     */
    public static UserAnswer voToObj(UserAnswerVO useranswerVO) {
        if (useranswerVO == null) {
            return null;
        }
        UserAnswer useranswer = new UserAnswer();
        BeanUtils.copyProperties(useranswerVO, useranswer);
        List<String> tagList = useranswerVO.getTagList();
//        useranswer.setTags(JSONUtil.toJsonStr(tagList));
        return useranswer;
    }

    /**
     * 对象转封装类
     *
     * @param useranswer
     * @return
     */
    public static UserAnswerVO objToVo(UserAnswer useranswer) {
        if (useranswer == null) {
            return null;
        }
        UserAnswerVO useranswerVO = new UserAnswerVO();
        BeanUtils.copyProperties(useranswer, useranswerVO);
//        useranswerVO.setTagList(JSONUtil.toList(useranswer.getTags(), String.class));
        return useranswerVO;
    }
}
