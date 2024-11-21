package com.heima.article.service;

import com.heima.common.mess.ArticleVisitStreamMess;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.behavior.dtos.CollectionBehaviorDto;
import com.heima.model.behavior.dtos.UserFollowDto;
import com.heima.model.common.dtos.ResponseResult;

public interface ArticlerFreemakerService {

    public void buildArticleToMinIO(ApArticle article,String content);

    ResponseResult getArticleList(UserFollowDto dto);

    void UserCollection(CollectionBehaviorDto dto);

    void UpdateHotArticle(ArticleVisitStreamMess mess);
}
