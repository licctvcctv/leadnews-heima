package com.heima.kafka.sample;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

/**
 * Kafka 消费者示例代码
 */
public class ConsumerQuickStart {

    public static void main(String[] args) {
        // 1. 配置 Kafka 消费者的基本配置信息
        Properties properties = configureKafkaConsumer();

        // 2. 创建 KafkaConsumer 对象
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);

        // 3. 订阅 Kafka 主题
        subscribeToTopic(consumer, "itcast-topic-out");

        // 4. 持续拉取并处理消息
        consumeMessages(consumer);
    }

    /**
     * 配置 Kafka 消费者的基本配置信息
     *
     * @return 配置好的 Kafka 配置
     */
    private static Properties configureKafkaConsumer() {
        Properties properties = new Properties();

        // Kafka 集群的连接地址
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.200.128:9092");

        // 消费者组 ID
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "group2");

        // 消息的反序列化器
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        // 设置是否自动提交偏移量，禁用自动提交，可以手动提交偏移量
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        return properties;
    }

    /**
     * 订阅 Kafka 主题
     *
     * @param consumer Kafka 消费者对象
     * @param topic    要订阅的主题
     */
    private static void subscribeToTopic(KafkaConsumer<String, String> consumer, String topic) {
        // 订阅指定的 Kafka 主题
        consumer.subscribe(Collections.singletonList(topic));
    }

    /**
     * 消费消息并处理
     *
     * @param consumer Kafka 消费者对象
     */
    private static void consumeMessages(KafkaConsumer<String, String> consumer) {
        // 持续拉取消息并处理
        while (true) {
            // 拉取消息，设置最大等待时间为 1000 毫秒
            ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMillis(1000));

            // 遍历拉取到的消息
            for (ConsumerRecord<String, String> consumerRecord : consumerRecords) {
                // 打印消息的 Key
                System.out.println("Message Key: " + consumerRecord.key());

                // 打印消息的 Value
                System.out.println("Message Value: " + consumerRecord.value());

                // 打印消息的偏移量
                System.out.println("Message Offset: " + consumerRecord.offset());

                // 打印消息所在的分区
                System.out.println("Message Partition: " + consumerRecord.partition());
            }

            // 提交偏移量：这里使用异步提交，确保消息消费完后偏移量能够及时提交
            commitOffsetsAsync(consumer);
        }
    }

    /**
     * 异步提交偏移量
     *
     * @param consumer Kafka 消费者对象
     */
    private static void commitOffsetsAsync(KafkaConsumer<String, String> consumer) {
        try {
            // 异步提交偏移量
            consumer.commitAsync();
        } catch (Exception e) {
            // 打印异常信息
            e.printStackTrace();
            System.out.println("异步提交偏移量失败: " + e.getMessage());
        } finally {
            // 在 finally 块中强制同步提交偏移量，确保消息的消费进度被更新
            consumer.commitSync();
        }
    }
}
