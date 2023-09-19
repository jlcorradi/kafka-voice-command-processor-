package dev.jlcorradi.streams.voice;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;

import java.util.Properties;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StreamsConfig {

  public static Properties createConfig() {
    Properties props = new Properties();
    props.put(org.apache.kafka.streams.StreamsConfig.APPLICATION_ID_CONFIG, "word-count-app");
    props.put(org.apache.kafka.streams.StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
    props.put(org.apache.kafka.streams.StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
    props.put(org.apache.kafka.streams.StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
    props.put(org.apache.kafka.streams.StreamsConfig.CACHE_MAX_BYTES_BUFFERING_DOC, "0");

    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "parsed-command-consumer-1");


    return props;
  }

}
