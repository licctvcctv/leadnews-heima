package com.heima.model.behavior.dtos;

import lombok.Data;

@Data
public class LikesBehaviorDto {

    private Long articleId;  // 文章ID
    private Short operation; // 点赞操作: 0 点赞, 1 取消点赞
    private Short type;
    private int count;
}