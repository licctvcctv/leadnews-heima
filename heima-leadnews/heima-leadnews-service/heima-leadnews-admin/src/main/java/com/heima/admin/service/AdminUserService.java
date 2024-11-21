package com.heima.admin.service;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.admin.dtos.AdminDto;

public interface AdminUserService {
    public ResponseResult login(AdminDto adminDto);
}
