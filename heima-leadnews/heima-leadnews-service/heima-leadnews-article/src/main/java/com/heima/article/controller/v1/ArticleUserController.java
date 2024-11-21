package com.heima.article.controller.v1;

import com.heima.article.service.ApArticleService;
import com.heima.article.service.ArticlerFreemakerService;
import com.heima.model.behavior.dtos.CollectionBehaviorDto;
import com.heima.model.behavior.dtos.UserFollowDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ArticleUserController {

    @Autowired
    private ArticlerFreemakerService articlerFreemakerService;

    @PostMapping("/collection_behavior")
    public ResponseResult LoadBehavior(@RequestBody CollectionBehaviorDto dto){

        articlerFreemakerService.UserCollection(dto);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
