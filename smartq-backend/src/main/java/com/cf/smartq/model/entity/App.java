package com.cf.smartq.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.Date;

/**
 * 應用
 * @TableName app
 */
@TableName(value ="app")
@Data
public class App implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 應用名
     */
    @NotBlank(message = "應用名不能為空")
    @Size(max = 50, message = "應用名不能超過50個字符")
    private String appName;

    /**
     * 應用描述
     */
    @NotBlank(message = "應用描述不能為空")
    @Size(max = 200, message = "應用描述不能超過200個字符")
    private String appDesc;

    /**
     * 應用圖標
     */
    @Pattern(regexp = "^(https?://)[\\w-]+(\\.[\\w-]+)+([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?$", message = "圖標URL格式不正確")
    private String appIcon;

    /**
     * 應用類型（0-得分類，1-測評類）
     */
    @NotNull(message = "應用類型不能為空")
    @Min(value = 0, message = "應用類型必須為 0 或 1")
    @Max(value = 1, message = "應用類型必須為 0 或 1")
    private Integer appType;

    /**
     * 評分策略（0-自定義，1-AI）
     */
    @NotNull(message = "評分策略不能為空")
    @Min(value = 0, message = "評分策略必須為 0 或 1")
    @Max(value = 1, message = "評分策略必須為 0 或 1")
    private Integer scoringStrategy;

    /**
     * 審核狀態：0-待審核, 1-通過, 2-拒絕
     */
    @Min(value = 0, message = "審核狀態必須為 0、1 或 2")
    @Max(value = 2, message = "審核狀態必須為 0、1 或 2")
    private Integer reviewStatus;

    /**
     * 審核信息
     */
    @Size(max = 500, message = "審核信息不能超過500個字符")
    private String reviewMessage;

    /**
     * 審核人 id
     */
    @Positive(message = "審核人ID必須為正數")
    private Long reviewerId;

    /**
     * 審核時間
     */
    @TableField(fill = FieldFill.INSERT)
    private Date reviewTime;

    /**
     * 創建用戶 id
     */
    @NotNull(message = "創建用戶ID不能為空")
    @Positive(message = "創建用戶ID必須為正數")
    private Long userId;

    /**
     * 創建時間
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新時間
     */
    @TableField(fill = FieldFill.INSERT)
    private Date updateTime;

    /**
     * 是否刪除
     */
    @Min(value = 0, message = "是否刪除標記必須為 0 或 1")
    @Max(value = 1, message = "是否刪除標記必須為 0 或 1")
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        App other = (App) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getAppName() == null ? other.getAppName() == null : this.getAppName().equals(other.getAppName()))
            && (this.getAppDesc() == null ? other.getAppDesc() == null : this.getAppDesc().equals(other.getAppDesc()))
            && (this.getAppIcon() == null ? other.getAppIcon() == null : this.getAppIcon().equals(other.getAppIcon()))
            && (this.getAppType() == null ? other.getAppType() == null : this.getAppType().equals(other.getAppType()))
            && (this.getScoringStrategy() == null ? other.getScoringStrategy() == null : this.getScoringStrategy().equals(other.getScoringStrategy()))
            && (this.getReviewStatus() == null ? other.getReviewStatus() == null : this.getReviewStatus().equals(other.getReviewStatus()))
            && (this.getReviewMessage() == null ? other.getReviewMessage() == null : this.getReviewMessage().equals(other.getReviewMessage()))
            && (this.getReviewerId() == null ? other.getReviewerId() == null : this.getReviewerId().equals(other.getReviewerId()))
            && (this.getReviewTime() == null ? other.getReviewTime() == null : this.getReviewTime().equals(other.getReviewTime()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
            && (this.getIsDelete() == null ? other.getIsDelete() == null : this.getIsDelete().equals(other.getIsDelete()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getAppName() == null) ? 0 : getAppName().hashCode());
        result = prime * result + ((getAppDesc() == null) ? 0 : getAppDesc().hashCode());
        result = prime * result + ((getAppIcon() == null) ? 0 : getAppIcon().hashCode());
        result = prime * result + ((getAppType() == null) ? 0 : getAppType().hashCode());
        result = prime * result + ((getScoringStrategy() == null) ? 0 : getScoringStrategy().hashCode());
        result = prime * result + ((getReviewStatus() == null) ? 0 : getReviewStatus().hashCode());
        result = prime * result + ((getReviewMessage() == null) ? 0 : getReviewMessage().hashCode());
        result = prime * result + ((getReviewerId() == null) ? 0 : getReviewerId().hashCode());
        result = prime * result + ((getReviewTime() == null) ? 0 : getReviewTime().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        result = prime * result + ((getIsDelete() == null) ? 0 : getIsDelete().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", appName=").append(appName);
        sb.append(", appDesc=").append(appDesc);
        sb.append(", appIcon=").append(appIcon);
        sb.append(", appType=").append(appType);
        sb.append(", scoringStrategy=").append(scoringStrategy);
        sb.append(", reviewStatus=").append(reviewStatus);
        sb.append(", reviewMessage=").append(reviewMessage);
        sb.append(", reviewerId=").append(reviewerId);
        sb.append(", reviewTime=").append(reviewTime);
        sb.append(", userId=").append(userId);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", isDelete=").append(isDelete);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}