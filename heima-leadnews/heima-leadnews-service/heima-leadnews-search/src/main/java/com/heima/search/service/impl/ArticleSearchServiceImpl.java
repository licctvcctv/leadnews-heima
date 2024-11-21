package com.heima.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.search.dtos.UserSearchDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.search.service.ApUserSearchService;
import com.heima.search.service.ArticleSearchService;
import com.heima.utils.thread.AppThreadLocalUtils;
import com.heima.utils.thread.WmThreadLocalUtils;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service // 将该类注册为 Spring 的 Service Bean
@Slf4j // 使用 Lombok 的注解，提供日志记录功能
public class ArticleSearchServiceImpl implements ArticleSearchService {

    @Autowired
    private RestHighLevelClient restHighLevelClient; // 注入 Elasticsearch 客户端，用于与 Elasticsearch 通信

    @Autowired
    private ApUserSearchService apUserSearchService;
    /**
     * 文章分页检索
     *
     * @param dto 包含搜索关键词和分页信息的 DTO 对象
     * @return 搜索结果封装在 ResponseResult 中
     */
    @Override
    public ResponseResult search(UserSearchDto dto) throws IOException {

        // 1. 检查参数是否有效，若关键词为空则返回错误结果
        if (dto == null || StringUtils.isBlank(dto.getSearchWords())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        log.info("搜索关键词为" + dto.getSearchWords());
        ApUser user = AppThreadLocalUtils.getUser();

        apUserSearchService.insert(dto.getSearchWords(), user.getId());

        // 2. 设置 Elasticsearch 查询条件
        SearchRequest searchRequest = new SearchRequest("app_info_article"); // 指定要查询的索引名称
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder(); // 构建查询请求

        // 布尔查询，用于组合多个查询条件
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 使用关键词分词查询，匹配 "title" 和 "content" 字段，默认操作符为 OR
        QueryStringQueryBuilder queryStringQueryBuilder = QueryBuilders
                .queryStringQuery(dto.getSearchWords())
                .field("title")
                .field("content")
                .defaultOperator(Operator.OR);
        boolQueryBuilder.must(queryStringQueryBuilder); // 将关键词查询添加到布尔查询的 must 条件中

        // 过滤条件：只查询小于指定时间的数据
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("publishTime")
                .lt(dto.getMinBehotTime().getTime());
        boolQueryBuilder.filter(rangeQueryBuilder); // 将时间过滤条件添加到布尔查询的 filter 条件中

        // 计算分页参数
        int from = dto.getPageNum() * dto.getPageSize();  // 计算分页的起始位置
        searchSourceBuilder.from(from);
        searchSourceBuilder.size(dto.getPageSize());  // 设置每页的条数

        // 排序：按发布时间倒序
        searchSourceBuilder.sort("publishTime", SortOrder.DESC);

        // 设置高亮显示关键字，仅高亮 "title" 字段
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title"); // 指定高亮字段
        highlightBuilder.preTags("<font style='color: red; font-size: inherit;'>"); // 设置高亮前缀
        highlightBuilder.postTags("</font>"); // 设置高亮后缀
        searchSourceBuilder.highlighter(highlightBuilder); // 添加高亮设置到查询请求中

        // 将布尔查询条件添加到查询构建器中
        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder); // 将查询构建器设置到查询请求中

        // 执行查询，获取搜索响应
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        // 3. 封装搜索结果并返回
        List<Map> list = new ArrayList<>(); // 用于存储每条记录的结果

        SearchHit[] hits = searchResponse.getHits().getHits(); // 获取查询到的所有记录
        for (SearchHit hit : hits) {
            String json = hit.getSourceAsString(); // 将记录转换为 JSON 格式
            Map map = JSON.parseObject(json, Map.class); // 将 JSON 转换为 Map

            // 处理高亮显示内容
            if (hit.getHighlightFields() != null && hit.getHighlightFields().size() > 0) {
                Text[] titles = hit.getHighlightFields().get("title").getFragments();
                StringBuilder titleBuilder = new StringBuilder();
                for (Text text : titles) {
                    titleBuilder.append(text.string());
                }
                map.put("h_title", titleBuilder.toString()); // 将高亮标题存入 Map 中
            } else {
                map.put("h_title", map.get("title")); // 若无高亮字段则使用原始标题
            }

            list.add(map); // 将每条记录添加到结果列表中
        }

        return ResponseResult.okResult(list); // 返回封装好的结果列表
    }
}
