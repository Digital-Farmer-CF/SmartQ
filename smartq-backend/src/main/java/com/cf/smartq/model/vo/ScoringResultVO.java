package com.cf.smartq.model.vo;

import cn.hutool.json.JSONUtil;
import com.cf.smartq.model.entity.ScoringResult;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 评分结果视图
 *

 */
@Data
public class ScoringResultVO implements Serializable {

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
     * @param scoringresultVO
     * @return
     */
    public static ScoringResult voToObj(ScoringResultVO scoringresultVO) {
        if (scoringresultVO == null) {
            return null;
        }
        ScoringResult scoringresult = new ScoringResult();
        BeanUtils.copyProperties(scoringresultVO, scoringresult);
        List<String> tagList = scoringresultVO.getTagList();
//        scoringresult.setTags(JSONUtil.toJsonStr(tagList));
        return scoringresult;
    }

    /**
     * 对象转封装类
     *
     * @param scoringresult
     * @return
     */
    public static ScoringResultVO objToVo(ScoringResult scoringresult) {
        if (scoringresult == null) {
            return null;
        }
        ScoringResultVO scoringresultVO = new ScoringResultVO();
        BeanUtils.copyProperties(scoringresult, scoringresultVO);
//        scoringresultVO.setTagList(JSONUtil.toList(scoringresult.getTags(), String.class));
        return scoringresultVO;
    }
}
