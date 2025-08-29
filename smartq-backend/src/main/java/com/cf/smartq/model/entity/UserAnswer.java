package com.cf.smartq.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.Date;

/**
 * 用戶答題記錄
 * @TableName user_answer
 */
@TableName(value ="user_answer")
@Data
public class UserAnswer implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 應用 id
     */
    @NotNull(message = "應用ID不能為空")
    @Positive(message = "應用ID必須為正數")
    private Long appId;

    /**
     * 應用類型（0-得分類，1-角色測評類）
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
     * 用戶答案（JSON 數組）
     */
    @NotBlank(message = "用戶答案不能為空")
    @Pattern(regexp = "^\\[.*\\]$", message = "用戶答案必須是有效的JSON數組格式")
    private String choices;

    /**
     * 評分結果 id
     */
    @Positive(message = "評分結果ID必須為正數")
    private Long resultId;

    /**
     * 結果名稱，如物流師
     */
    @Size(max = 50, message = "結果名稱不能超過50個字符")
    private String resultName;

    /**
     * 結果描述
     */
    @Size(max = 500, message = "結果描述不能超過500個字符")
    private String resultDesc;

    /**
     * 結果圖標
     */
    @Pattern(regexp = "^(https?://)[\\w-]+(\\.[\\w-]+)+([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?$", message = "圖標URL格式不正確")
    private String resultPicture;

    /**
     * 得分
     */
    @Min(value = 0, message = "得分不能小於0")
    @Max(value = 100, message = "得分不能大於100")
    private Integer resultScore;

    /**
     * 用戶 id
     */
    @NotNull(message = "用戶ID不能為空")
    @Positive(message = "用戶ID必須為正數")
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
        UserAnswer other = (UserAnswer) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getAppId() == null ? other.getAppId() == null : this.getAppId().equals(other.getAppId()))
            && (this.getAppType() == null ? other.getAppType() == null : this.getAppType().equals(other.getAppType()))
            && (this.getScoringStrategy() == null ? other.getScoringStrategy() == null : this.getScoringStrategy().equals(other.getScoringStrategy()))
            && (this.getChoices() == null ? other.getChoices() == null : this.getChoices().equals(other.getChoices()))
            && (this.getResultId() == null ? other.getResultId() == null : this.getResultId().equals(other.getResultId()))
            && (this.getResultName() == null ? other.getResultName() == null : this.getResultName().equals(other.getResultName()))
            && (this.getResultDesc() == null ? other.getResultDesc() == null : this.getResultDesc().equals(other.getResultDesc()))
            && (this.getResultPicture() == null ? other.getResultPicture() == null : this.getResultPicture().equals(other.getResultPicture()))
            && (this.getResultScore() == null ? other.getResultScore() == null : this.getResultScore().equals(other.getResultScore()))
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
        result = prime * result + ((getAppId() == null) ? 0 : getAppId().hashCode());
        result = prime * result + ((getAppType() == null) ? 0 : getAppType().hashCode());
        result = prime * result + ((getScoringStrategy() == null) ? 0 : getScoringStrategy().hashCode());
        result = prime * result + ((getChoices() == null) ? 0 : getChoices().hashCode());
        result = prime * result + ((getResultId() == null) ? 0 : getResultId().hashCode());
        result = prime * result + ((getResultName() == null) ? 0 : getResultName().hashCode());
        result = prime * result + ((getResultDesc() == null) ? 0 : getResultDesc().hashCode());
        result = prime * result + ((getResultPicture() == null) ? 0 : getResultPicture().hashCode());
        result = prime * result + ((getResultScore() == null) ? 0 : getResultScore().hashCode());
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
        sb.append(", appId=").append(appId);
        sb.append(", appType=").append(appType);
        sb.append(", scoringStrategy=").append(scoringStrategy);
        sb.append(", choices=").append(choices);
        sb.append(", resultId=").append(resultId);
        sb.append(", resultName=").append(resultName);
        sb.append(", resultDesc=").append(resultDesc);
        sb.append(", resultPicture=").append(resultPicture);
        sb.append(", resultScore=").append(resultScore);
        sb.append(", userId=").append(userId);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", isDelete=").append(isDelete);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}