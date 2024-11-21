package com.heima.kafka.HelloListener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

// 声明该类是一个 Spring 组件，Spring 容器会自动扫描并管理该类
@Component
public class HelloListener {

    // 使用 @KafkaListener 注解标记此方法是一个 Kafka 消息监听器
    // 该方法会监听名为 "itcast-topic" 的 Kafka 主题
    @KafkaListener(topics = "itcast-topic")
    public void onMessage(String message) {
        // 判断传入的消息是否为空或空字符串
        // StringUtils.isEmpty 是 Spring 提供的工具方法，用于检查字符串是否为 null 或空字符串
        if (!StringUtils.isEmpty(message)) {
            // 如果消息不为空，则输出消息内容
            System.out.println(message);
        }
    }
}
