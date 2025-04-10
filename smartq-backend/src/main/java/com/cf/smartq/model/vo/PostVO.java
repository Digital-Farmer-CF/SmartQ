package com.cf.smartq.model.vo;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.cf.smartq.model.entity.Post;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 帖子视图
 *

 */
    @Data
    public class PostVO implements Serializable {

        /**
         * id
         */
        @TableId(type = IdType.ASSIGN_ID)
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
         * 标签列表 json
         */
        private String tags;

        /**
         * 点赞数
         */
        private Integer thumbNum;

        /**
         * 收藏数
         */
        private Integer favourNum;

        /**
         * 创建时间
         */
        private Date createTime;

        /**
         * 更新时间
         */
        private Date updateTime;

        /**
         * 展示用户信息(昵称,头像)(增加)
         */
        private UserVO user;
}
