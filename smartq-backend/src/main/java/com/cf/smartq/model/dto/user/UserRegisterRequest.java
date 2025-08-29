package com.cf.smartq.model.dto.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.Date;

/**
 * 用戶註冊請求體
 *

 */
@Data
public class UserRegisterRequest implements Serializable {

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用戶賬號
     */
    @NotBlank(message = "用戶賬號不能為空")
    @Size(min = 4, message = "用戶賬號長度不能小於4位")
    private String userAccount;

    /**
     * 用戶密碼
     */
    @NotBlank(message = "密碼不能為空")
    @Size(min = 8, message = "密碼長度不能小於8位")
    private String userPassword;

    /**
     * 開放平台id
     */
    private String unionId;

//    /**
//     * 公众号openId
//     */
//    private String mpOpenId;

    /**
     * 用戶暱稱
     */
    @NotBlank(message = "用戶暱稱不能為空")
    @Size(max = 50, message = "用戶暱稱長度不能超過50個字符")
    private String userName;

    /**
     * 用戶頭像
     */
    @Pattern(regexp = "^(https?://)[\\w-]+(\\.[\\w-]+)+([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?$", message = "頭像URL格式不正確")
    private String userAvatar;

    /**
     * 用戶簡介
     */
    @Size(max = 200, message = "用戶簡介不能超過200個字符")
    private String userProfile;

    /**
     * 用戶角色：user/admin/ban
     */
    @Pattern(regexp = "^(user|admin|ban)$", message = "用戶角色只能是 user、admin 或 ban")
    private String userRole;

    /**
     * 創建時間
     */
    private Date createTime;

    /**
     * 是否刪除
     */
    @TableLogic
    private Integer isDelete;

    /**
     * 確認密碼
     */
    @NotBlank(message = "確認密碼不能為空")
    private String CheckPassword;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
