package com.heima.kafka.sample;


import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.ValueMapper;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class kafkaSteamQuickStart {
    public static void main(String[] args) {
        Properties properties = new Properties();

        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.200.128:9092");

        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG,Serdes.String().getClass());

        properties.put(StreamsConfig.APPLICATION_ID_CONFIG,"steams-quickstart");

        StreamsBuilder streamsBuilder = new StreamsBuilder();

        SteamProcessor(streamsBuilder);

        KafkaStreams kafkaStreams = new KafkaStreams(streamsBuilder.build(),properties);

        kafkaStreams.start();
    }

    private static void SteamProcessor(StreamsBuilder streamsBuilder) {

        KStream<String, String> stream = streamsBuilder.stream("itcast-topic-input");

        stream.flatMapValues(new ValueMapper<String, Iterable<String>>() {
            @Override
            public Iterable<String> apply(String values) {


                String[] split = values.split(" ");
                return Arrays.asList(split);
            }
        })
                .groupBy((key,value) -> value)

                .windowedBy(TimeWindows.of(Duration.ofSeconds(10)))

                .count()

                .toStream()

                .map((key,value) -> {
                    System.out.println("key:" + key + " value" + value);
                    return new KeyValue<>(key.key().toString(),value.toString());
                })

                .to("itcast-topic-out");




    }
}
