package com.llyinatech.houserental.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户查询参数
 */
@Data
@Schema(description = "用户查询参数")
public class UserQueryDto {

    @Schema(description = "页码", defaultValue = "1")
    private Integer current = 1;

    @Schema(description = "每页大小", defaultValue = "10")
    private Integer size = 10;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "状态")
    private Integer status;
}
