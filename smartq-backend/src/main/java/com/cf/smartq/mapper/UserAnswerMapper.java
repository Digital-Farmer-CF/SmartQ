package com.cf.smartq.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cf.smartq.model.dto.statistic.AppAnswerCountDTO;
import com.cf.smartq.model.dto.statistic.UserResultCountDTO;
import com.cf.smartq.model.entity.UserAnswer;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author sun_l
* @description 针对表【user_answer(用户答题记录)】的数据库操作Mapper
* @createDate 2025-04-02 14:04:51
* @Entity generator1.domain.UserAnswer
*/
public interface UserAnswerMapper extends BaseMapper<UserAnswer> {
    /**
     * 统计热门应用
     */

    @Select("select appId, count(userId) as answerCount from user_answer " +
            "group by appId order by answerCount desc")
    List<AppAnswerCountDTO> doAppAnswerCount();


    /**
     * 应用回答分布统计
     */
    @Select("select resultName, count(resultName) as resultCount from user_answer " +
            "where appId = #{appId} group by resultName order by resultCount desc")
    List<UserResultCountDTO> doAppAnswerResultCount(Long appId);


}



