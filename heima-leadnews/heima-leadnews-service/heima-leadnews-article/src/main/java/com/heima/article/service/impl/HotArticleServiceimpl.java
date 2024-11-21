package com.heima.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.heima.apis.wemedia.IwemediaClient;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ApArticleService;
import com.heima.article.service.HotArticleService;
import com.heima.common.constants.ArticleConstants;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleHot;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
@Service
@Transactional
@Slf4j
public class HotArticleServiceimpl implements HotArticleService {

    @Autowired
    private ApArticleMapper apArticleMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private IwemediaClient iwemediaClient;

    @Override
    public void computerArticle() {
        // 查找五天以内的文章
        LocalDate date = LocalDate.now().minusDays(5);
        List<ApArticle> apArticles = apArticleMapper.selectList(new LambdaQueryWrapper<ApArticle>().ge(ApArticle::getCreatedTime, date));

        if (apArticles != null && !apArticles.isEmpty()) {
            List<ApArticleHot> apArticleHots = new ArrayList<>();
            for (ApArticle apArticle : apArticles) {
                ApArticleHot apArticleHot = new ApArticleHot();
                BeanUtils.copyProperties(apArticle, apArticleHot);
                int scope = calculateScope(apArticle);  // 使用优化后的方法
                apArticleHot.setScope(scope);
                apArticleHots.add(apArticleHot);
            }
            toHotArticleRedis(apArticleHots);
        } else {
            log.warn("No articles found in the last 5 days.");
        }
    }

    // 计算文章的热度分数，防止 Integer 为 null
    private int calculateScope(ApArticle apArticle) {
        // 使用三元运算符进行 null 检查，如果为 null 则用 0 替代
        int likes = (apArticle.getLikes() != null) ? apArticle.getLikes() : 0;
        int comment = (apArticle.getComment() != null) ? apArticle.getComment() : 0;
        int collection = (apArticle.getCollection() != null) ? apArticle.getCollection() : 0;

        return likes * ArticleConstants.HOT_ARTICLE_LIKE_WEIGHT
                + comment * ArticleConstants.HOT_ARTICLE_COMMENT_WEIGHT
                + collection * ArticleConstants.HOT_ARTICLE_COLLECTION_WEIGHT;
    }

    private void toHotArticleRedis(List<ApArticleHot> apArticleHots) {
        try {
            ResponseResult channelList = iwemediaClient.getChannelList();
            if (channelList.getCode() == 200 && channelList.getData() != null) {
                String jsonString = JSON.toJSONString(channelList.getData());
                List<WmChannel> wmChannels = JSON.parseArray(jsonString, WmChannel.class);

                if (wmChannels != null && !wmChannels.isEmpty()) {
                    for (WmChannel wmChannel : wmChannels) {
                        String channelName = wmChannel.getName();
                        if (channelName != null) {
                            List<ApArticleHot> filteredArticles = apArticleHots.stream()
                                    .filter(apArticleHot -> channelName.equals(apArticleHot.getChannelName()))
                                    .sorted((a1, a2) -> Integer.compare(a2.getScope(), a1.getScope()))
                                    .collect(Collectors.toList());

                            if (!filteredArticles.isEmpty()) {
                                filteredArticles = filteredArticles.size() > 30 ? filteredArticles.subList(0,30):filteredArticles;
                                redisTemplate.opsForValue().set(ArticleConstants.HOT_ARTICLE_FIRST_PAGE + wmChannel.getId(), JSON.toJSONString(filteredArticles));
                                log.info("Hot articles for channel '{}' stored in Redis.", channelName);
                            }
                        } else {
                            log.warn("Encountered a null channel name, skipping...");
                        }
                    }
                } else {
                    log.warn("No valid channels found.");
                }

                // 存储所有文章的热度信息
                List<ApArticleHot> sortedArticles = apArticleHots.stream()
                        .sorted((a1, a2) -> Integer.compare(a2.getScope(), a1.getScope()))
                        .collect(Collectors.toList());

                if (!sortedArticles.isEmpty()) {
                    sortedArticles = sortedArticles.size() >=30 ? sortedArticles.subList(0,30):sortedArticles;
                    redisTemplate.opsForValue().set(ArticleConstants.HOT_ARTICLE_FIRST_PAGE + ArticleConstants.DEFAULT_TAG, JSON.toJSONString(sortedArticles));
                    log.info("Hot articles for default tag stored in Redis.");
                }
            } else {
                log.error("Failed to fetch channel list from media service. Code: {}", channelList.getCode());
            }
        } catch (Exception e) {
            log.error("Error occurred while saving hot articles to Redis.", e);
        }
    }
}
