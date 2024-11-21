package com.heima.kafka.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * HelloController 类，负责处理 HTTP 请求并将消息发送到 Kafka。
 */
@RestController // 表示这是一个控制器类，返回的数据会直接映射到 HTTP 响应体中
public class HelloController {

    // 自动注入 KafkaTemplate，用于发送消息到 Kafka 集群
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 处理 /hello 请求的 GET 请求方法。
     * 当访问该接口时，向 Kafka 发送一条消息。
     *
     * @return 返回 "ok" 表示消息已经发送成功
     */
    @GetMapping("/hello") // 处理 HTTP GET 请求，路径为 "/hello"
    public String hello() {
        // 发送消息到 Kafka
        // "itcast-topic" 是目标 Kafka 主题
        // "黑马程序员" 是消息内容
        kafkaTemplate.send("itcast-topic", "黑马程序员");

        // 返回响应
        // 这里返回 "ok"，表示请求已经处理完成
        return "ok";
    }
}
