package com.heima.kafka.sample;

import org.apache.kafka.clients.producer.*;

import java.util.Properties;

/**
 * Kafka 生产者示例代码
 */
public class ProducerQuickStart {

    public static void main(String[] args) {
        // 1. 配置 Kafka 生产者的基本配置信息
        Properties properties = new Properties();

        // 设置 Kafka 集群的地址（Bootstrap Servers），通常是一个或多个 Kafka Broker 地址
        // 用于初始化与 Kafka 集群的连接
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.200.128:9092");

        // 设置发送消息失败后的重试次数
        // 如果消息发送失败，Kafka 会重试多次（最多 5 次）
        properties.put(ProducerConfig.RETRIES_CONFIG, 5);

        // 设置消息的 Key（消息键）的序列化器
        // Kafka 生产者需要将消息的 Key（键）序列化为字节数组
        // 这里使用的是 String 类型，所以使用 StringSerializer 来进行序列化
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        // 设置消息的 Value（消息值）的序列化器
        // 同样，消息值也需要被序列化为字节数组
        // 这里也使用 String 类型的序列化器
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");


        // 2. 创建 KafkaProducer 对象
        // 使用上面配置的 Properties 对象来初始化 KafkaProducer
        // KafkaProducer 是线程安全的，可以在多个线程中共享

        properties.put(ProducerConfig.ACKS_CONFIG,"all");

        properties.put(ProducerConfig.RETRIES_CONFIG,10);

        properties.put(ProducerConfig.COMPRESSION_TYPE_CONFIG,"lz4");

        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        // 3. 构造发送的消息
        // Kafka 消息封装在 ProducerRecord 中
        // 第一个参数是目标 Topic 名称
        // 第二个参数是消息的 Key（在这里是 "100001"），用于分区选择
        // 第三个参数是消息的 Value（在这里是 "hello kafka"），即消息内容
        ProducerRecord<String, String> kvProducerRecord = new ProducerRecord<>("itcast-topic-input", "100001", "hello kafka");


        // 4. 发送消息
        // 使用 KafkaProducer 的 send 方法将消息发送到 Kafka
        // send 方法是异步的，意味着它会将消息发送到 Kafka 后立即返回，不等待发送结果
        // send 方法是非阻塞的，KafkaProducer 会处理后台的网络通信
        //异步消息发送
        for (int i = 0; i < 10; i++) {
            producer.send(kvProducerRecord, new Callback() {
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    if(e != null){
                        System.out.println("记录异常信息到日志表中");
                    }
                    System.out.println(recordMetadata.offset());
                }
            });

        }


        // 5. 关闭生产者连接
        // KafkaProducer 必须关闭，否则一些缓存中的消息可能没有被成功发送到 Kafka
        // 如果在关闭时不发送完所有消息，可能会丢失消息
        producer.close();
    }
}
