package dev.jlcorradi.streams.voice.config;

import dev.jlcorradi.streams.voice.VoiceCommandParserTopology;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.Topology;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@Configuration
@EnableKafkaStreams
public class KafkaStreamsConfig {

  @Bean
  public KafkaStreams kafkaStreams(VoiceCommandParserTopology voiceCommandParserTopology,
                                   StreamsProperties props) {
    Topology topology = voiceCommandParserTopology.createTopology();
    return new KafkaStreams(topology, props.createConfig());
  }
}
