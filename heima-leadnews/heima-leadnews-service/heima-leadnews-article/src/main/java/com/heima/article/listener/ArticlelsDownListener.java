package com.heima.article.listener;

import com.alibaba.fastjson.JSON;
import com.heima.article.service.ApArticleConfigService;
import com.heima.article.service.impl.ArticlerFreemakerServiceimpl;
import com.heima.common.constants.HotArticleConstants;
import com.heima.common.constants.WmNewsMessageConstants;
import com.heima.common.mess.ArticleVisitStreamMess;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


import java.util.Map;

@Component
@Slf4j
public class ArticlelsDownListener {

    @Autowired
    private ApArticleConfigService apArticleConfigService;
    // 声明该类是一个 Spring 组件，Spring 容器会自动扫描并管理该类


        // 使用 @KafkaListener 注解标记此方法是一个 Kafka 消息监听器
        // 该方法会监听名为 "itcast-topic" 的 Kafka 主题
        @KafkaListener(topics = WmNewsMessageConstants.WM_NEWS_UP_OR_DOWN_TOPIC)
        public void onMessage(String message) {
            // 判断传入的消息是否为空或空字符串
            // StringUtils.isEmpty 是 Spring 提供的工具方法，用于检查字符串是否为 null 或空字符串
            if (!StringUtils.isEmpty(message)) {
                // 如果消息不为空，则输出消息内容
                Map map = JSON.parseObject(message, Map.class);
                log.info("上下架" + map.get("articleId"));
                apArticleConfigService.updateByMap(map);

            }
        }

        @Autowired
        private ArticlerFreemakerServiceimpl articlerFreemakerServiceimpl;

        @KafkaListener(topics = HotArticleConstants.HOT_ARTICLE_INCR_HANDLE_TOPIC)
        public void ArtMessage(String message) {
            // 判断传入的消息是否为空或空字符串
            // StringUtils.isEmpty 是 Spring 提供的工具方法，用于检查字符串是否为 null 或空字符串
            if (StringUtils.isNotBlank(message)) {
                log.info(message);
                // 如果消息不为空，则输出消息内容
                ArticleVisitStreamMess mess = JSON.parseObject(message, ArticleVisitStreamMess.class);
                articlerFreemakerServiceimpl.UpdateHotArticle(mess);

            }
        }


}
