package com.heima.article.job;

import com.heima.article.service.HotArticleService;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ArticleJob {

    @Autowired
    private HotArticleService hotArticleService;
    @XxlJob("ArticleJob")
    private void hotArticle(){
        hotArticleService.computerArticle();
    }
}
