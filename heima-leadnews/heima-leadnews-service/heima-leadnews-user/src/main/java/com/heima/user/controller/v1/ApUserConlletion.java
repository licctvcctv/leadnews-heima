package com.heima.user.controller.v1;

import com.heima.model.behavior.dtos.UserFollowDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.user.service.ApUserFollow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@Slf4j
public class ApUserConlletion {
    @Autowired
    private ApUserFollow apUserFollow;

    @PostMapping("/user_follow")
    public ResponseResult UserFollow(@RequestBody UserFollowDto dto){
        apUserFollow.UserFollow(dto);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
