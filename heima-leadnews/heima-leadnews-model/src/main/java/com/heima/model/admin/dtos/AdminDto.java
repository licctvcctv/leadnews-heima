package com.heima.model.admin.dtos;


import lombok.Data;

@Data
public class AdminDto {

    /**
     * 用户名
     */
    private String name;
    /**
     * 密码
     */
    private String password;
}
