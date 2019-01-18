package com.org.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("sys_role_user")
public class UserRole implements Serializable {

    private static final long serialVersionUID = -277182870522231750L;

    @ApiModelProperty(value = "用户Id")
    private String userId;

    @ApiModelProperty(value = "角色Id")
    private String roleId;

}