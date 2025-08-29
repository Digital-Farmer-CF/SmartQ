package com.cf.smartq.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.Date;

/**
 * 評分結果
 * @TableName scoring_result
 */
@TableName(value ="scoring_result")
@Data
public class ScoringResult implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 結果名稱，如物流師
     */
    @NotBlank(message = "結果名稱不能為空")
    @Size(max = 50, message = "結果名稱不能超過50個字符")
    private String resultName;

    /**
     * 結果描述
     */
    @NotBlank(message = "結果描述不能為空")
    @Size(max = 500, message = "結果描述不能超過500個字符")
    private String resultDesc;

    /**
     * 結果圖片
     */
    @Pattern(regexp = "^(https?://)[\\w-]+(\\.[\\w-]+)+([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?$", message = "圖片URL格式不正確")
    private String resultPicture;

    /**
     * 結果屬性集合 JSON，如 [I,S,T,J]
     */
    @Pattern(regexp = "^\\[.*\\]$", message = "結果屬性必須是有效的JSON數組格式")
    private String resultProp;

    /**
     * 結果得分範圍，如 80，表示 80及以上的分數命中此結果
     */
    @NotNull(message = "結果得分範圍不能為空")
    @Min(value = 0, message = "結果得分範圍不能小於0")
    @Max(value = 100, message = "結果得分範圍不能大於100")
    private Integer resultScoreRange;

    /**
     * 應用 id
     */
    @NotNull(message = "應用ID不能為空")
    @Positive(message = "應用ID必須為正數")
    private Long appId;

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
        ScoringResult other = (ScoringResult) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getResultName() == null ? other.getResultName() == null : this.getResultName().equals(other.getResultName()))
            && (this.getResultDesc() == null ? other.getResultDesc() == null : this.getResultDesc().equals(other.getResultDesc()))
            && (this.getResultPicture() == null ? other.getResultPicture() == null : this.getResultPicture().equals(other.getResultPicture()))
            && (this.getResultProp() == null ? other.getResultProp() == null : this.getResultProp().equals(other.getResultProp()))
            && (this.getResultScoreRange() == null ? other.getResultScoreRange() == null : this.getResultScoreRange().equals(other.getResultScoreRange()))
            && (this.getAppId() == null ? other.getAppId() == null : this.getAppId().equals(other.getAppId()))
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
        result = prime * result + ((getResultName() == null) ? 0 : getResultName().hashCode());
        result = prime * result + ((getResultDesc() == null) ? 0 : getResultDesc().hashCode());
        result = prime * result + ((getResultPicture() == null) ? 0 : getResultPicture().hashCode());
        result = prime * result + ((getResultProp() == null) ? 0 : getResultProp().hashCode());
        result = prime * result + ((getResultScoreRange() == null) ? 0 : getResultScoreRange().hashCode());
        result = prime * result + ((getAppId() == null) ? 0 : getAppId().hashCode());
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
        sb.append(", resultName=").append(resultName);
        sb.append(", resultDesc=").append(resultDesc);
        sb.append(", resultPicture=").append(resultPicture);
        sb.append(", resultProp=").append(resultProp);
        sb.append(", resultScoreRange=").append(resultScoreRange);
        sb.append(", appId=").append(appId);
        sb.append(", userId=").append(userId);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", isDelete=").append(isDelete);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}