package com.heima.model.behavior.dtos;

import lombok.Data;

import java.util.Date;

@Data
public class CollectionBehaviorDto {

    // 文章ID
    private Long entryId;

    // 操作类型 (0: 收藏, 1: 取消收藏)
    private Short operation;

    // 发布时间
    private Date publishedTime;

    // 类型 (0: 文章, 1: 动态)
    private Short type;
}