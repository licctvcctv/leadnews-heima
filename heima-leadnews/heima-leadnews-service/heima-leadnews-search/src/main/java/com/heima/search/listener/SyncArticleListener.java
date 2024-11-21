package com.heima.search.listener;

import com.alibaba.fastjson.JSON;
import com.heima.common.constants.ArticleConstants;
import com.heima.model.search.vos.SearchArticleVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class SyncArticleListener {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    // 监听Kafka主题，获取文章同步消息
    @KafkaListener(topics = ArticleConstants.ARTICLE_ES_SYNC_TOPIC)
    public void onMessage(String message){
        // 检查消息是否不为空
        if (StringUtils.isNotBlank(message)){
            log.info(message.toString());
            // 将消息字符串转换为SearchArticleVo对象
            SearchArticleVo vo = JSON.parseObject(message, SearchArticleVo.class);
            // 创建Elasticsearch索引请求，并设置索引名称为“app_info_article”
            IndexRequest appInfoArticle = new IndexRequest("app_info_article");
            // 设置文档ID为文章的ID
            appInfoArticle.id(vo.getId().toString());
            // 将消息内容以JSON格式添加到索引请求的源中
            appInfoArticle.source(message, XContentType.JSON);

            try {
                // 发送索引请求，将数据同步到Elasticsearch
                IndexResponse index = restHighLevelClient.index(appInfoArticle, RequestOptions.DEFAULT);
                log.info("index" + index);
            } catch (IOException e) {
                // 处理异常，抛出运行时异常
                throw new RuntimeException(e);
            }
        }
    }
}
