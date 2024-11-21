package com.heima.admin.controller.v1;

import com.heima.admin.service.AdminUserService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.admin.dtos.AdminDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
@Slf4j
public class LoginController {
    @Autowired
    private AdminUserService adminUserService;

    @PostMapping("/in")
    public ResponseResult login(@RequestBody AdminDto adminDto){
        return adminUserService.login(adminDto);
    }
}
