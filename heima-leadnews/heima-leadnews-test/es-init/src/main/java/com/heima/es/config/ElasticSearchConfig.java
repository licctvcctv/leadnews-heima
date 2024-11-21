package com.heima.es.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration//标记为 @Configuration 的类会在 Spring 容器初始化时创建，并将该类中的 @Bean 方法返回的实例作为单例（Singleton）存储到 Spring 容器中，供其他地方依赖注入使用。因此，@Configuration 类返回的 @Bean 对象默认是单例的
@ConfigurationProperties(prefix = "elasticsearch")//@ConfigurationProperties 是 Spring Boot 的一个注解，用于将配置文件中的属性绑定到 Java 对象的字段上。
//prefix = "elasticsearch" 表示配置文件中以 elasticsearch 为前缀的属性会自动映射到这个类的字段上。
public class ElasticSearchConfig {

    private String host;    // Elasticsearch服务器的主机名或IP地址
    private int port;       // Elasticsearch服务器的端口号


    // 定义一个方法来创建 RestHighLevelClient 的实例，并将其注册为一个 Spring Bean
    @Bean
    public RestHighLevelClient client() {
        // 创建并返回 RestHighLevelClient 实例，用于与 Elasticsearch 服务器通信
        return new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(
                                host,
                                port,
                                "http")
                )
        );
    }
}
