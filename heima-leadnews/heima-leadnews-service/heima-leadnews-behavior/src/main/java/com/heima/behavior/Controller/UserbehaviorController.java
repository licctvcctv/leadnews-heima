package com.heima.behavior.Controller;

import com.heima.behavior.Service.BehaviorService;
import com.heima.behavior.Service.UserBehaviorService;
import com.heima.model.behavior.dtos.CollectionBehaviorDto;
import com.heima.model.behavior.dtos.LikesBehaviorDto;
import com.heima.model.behavior.dtos.ReadDto;

import com.heima.model.behavior.dtos.UserFollowDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.utils.thread.WmThreadLocalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class UserbehaviorController {
    @Autowired
    private UserBehaviorService userBehaviorService;
    @Autowired
    private BehaviorService behaviorService;
    @PostMapping("/likes_behavior")
    public ResponseResult like(@RequestBody LikesBehaviorDto Dto){
       behaviorService.saveLikeBehavior(Dto);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @PostMapping("/read_behavior")
    public ResponseResult ReadCount(@RequestBody ReadDto Dto){
        behaviorService.ReadCount(Dto);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @PostMapping("/un_likes_behavior")
    public ResponseResult UserFollow(@RequestBody LikesBehaviorDto Dto){
        behaviorService.noLoveArticle(Dto);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }







}
