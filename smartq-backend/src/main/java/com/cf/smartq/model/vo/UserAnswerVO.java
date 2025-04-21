package com.cf.smartq.model.vo;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
     *
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 应用 id
     */
    private Long appId;

    /**
     * 应用类型（0-得分类，1-角色测评类）
     */
    private Integer appType;

    /**
     * 评分策略（0-自定义，1-AI）
     */
    private Integer scoringStrategy;

    /**
     * 用户答案（JSON 数组）
     */
    private List<String> choices;

    /**
     * 评分结果 id
     */
    private Long resultId;

    /**
     * 结果名称，如物流师
     */
    private String resultName;

    /**
     * 结果描述
     */
    private String resultDesc;

    /**
     * 结果图标
     */
    private String resultPicture;

    /**
     * 得分
     */
    private Integer resultScore;

    /**
     * 创建时间
     */
    private Date createTime;

    private  UserVO userVO;

    private  Long userId;
//
//    /**
//     * 封装类转对象
//     *
//     * @param useranswerVO
//     * @return
//     */
//    public static UserAnswer voToObj(UserAnswerVO useranswerVO) {
//        if (useranswerVO == null) {
//            return null;
//        }
//        UserAnswer useranswer = new UserAnswer();
//        BeanUtils.copyProperties(useranswerVO, useranswer);
//        List<String> tagList = useranswerVO.getTagList();
////        useranswer.setTags(JSONUtil.toJsonStr(tagList));
//        return useranswer;
//    }

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
        useranswerVO.setChoices(JSONUtil.toList(useranswer.getChoices(), String.class));
        return useranswerVO;
    }
}
