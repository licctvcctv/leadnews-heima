package com.heima.es.pojo;

import lombok.Data;
import java.util.Date;

@Data
public class SearchArticleVo {

    // 文章 ID
    private Long id;

    // 文章标题
    private String title;

    // 文章发布时间
    private Date publishTime;

    // 布局类型
    private Integer layout;

    // 图片路径
    private String images;

    // 作者 ID
    private Long authorId;

    // 作者名称
    private String authorName;

    // 静态 URL
    private String staticUrl;

    // 文章内容
    private String content;
}
