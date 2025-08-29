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
 * 用戶登錄請求
 *

 */
@Data
public class UserLoginRequest implements Serializable {

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

    /**
     * 公眾號openId
     */
    private String mpOpenId;

    /**
     * 用戶角色：user/admin/ban
     */
    @Pattern(regexp = "^(user|admin|ban)$", message = "用戶角色只能是 user、admin 或 ban")
    private String userRole;

}
