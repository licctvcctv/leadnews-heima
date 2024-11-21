package com.heima.es;

import com.alibaba.fastjson.JSON;
import com.heima.es.mapper.ApArticleMapper;
import com.heima.es.pojo.SearchArticleVo;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * 测试类：用于将数据库中符合条件的文章数据批量导入到 Elasticsearch 索引库中。
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ApArticleTest {

    // 注入 MyBatis Mapper，用于查询文章数据
    @Autowired
    private ApArticleMapper apArticleMapper;

    // 注入 Elasticsearch 客户端，用于与 Elasticsearch 交互
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * init 方法：将数据库中的文章数据批量导入到 Elasticsearch 索引库中。
     * 注意：若数据量过大，建议分页处理以防止内存溢出。
     * @throws Exception 异常处理
     */
    @Test
    public void init() throws Exception {

        // 1. 从数据库中查询所有符合条件的文章数据
        List<SearchArticleVo> searchArticleVos = apArticleMapper.loadArticleList();

        // 2. 创建一个 BulkRequest，用于批量导入数据
        BulkRequest bulkRequest = new BulkRequest("app_info_article");

        // 遍历查询结果，将每条数据转换为 Elasticsearch 文档
        for (SearchArticleVo searchArticleVo : searchArticleVos) {
            // 创建 IndexRequest，指定文档 ID 为文章的 ID，将数据转为 JSON 格式
            IndexRequest indexRequest = new IndexRequest().id(searchArticleVo.getId().toString())
                    .source(JSON.toJSONString(searchArticleVo), XContentType.JSON);

            // 将 indexRequest 添加到 bulkRequest 中
            bulkRequest.add(indexRequest);
        }

        // 3. 执行批量导入操作，将数据插入到 Elasticsearch 索引库中
        restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
    }
}
