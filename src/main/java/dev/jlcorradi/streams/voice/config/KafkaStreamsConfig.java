package dev.jlcorradi.streams.voice.config;

import dev.jlcorradi.streams.voice.VoiceCommandParserTopology;
import dev.jlcorradi.streams.voice.model.ParsedVoiceCommand;
import dev.jlcorradi.streams.voice.model.VoiceCommand;
import dev.jlcorradi.streams.voice.serdes.VoiceCommandSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Collections;

@Configuration
@EnableKafkaStreams
public class KafkaStreamsConfig {

  @Bean
  public KafkaAdmin.NewTopics voiceCommandsTopic() {
    return new KafkaAdmin.NewTopics(
        TopicBuilder.name(VoiceCommandParserTopology.VOICE_COMMANDS_TOPIC)
            .partitions(1)
            .replicas(1)
            .build(),
        TopicBuilder.name(VoiceCommandParserTopology.RECOGNIZED_COMMANDS_TOPIC)
            .partitions(2)
            .replicas(1)
            .build(),
        TopicBuilder.name(VoiceCommandParserTopology.UNRECOGNIZED_COMMANDS_TOPIC)
            .partitions(2)
            .replicas(1)
            .build()
    );
  }

  @Bean
  public KafkaTemplate<String, VoiceCommand> voiceCommandKafkaTemplate(ProducerFactory<String, VoiceCommand> pf) {
    return new KafkaTemplate<>(pf,
        Collections.singletonMap(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, VoiceCommandSerializer.class.getName()));
  }

  @Bean
  public Deserializer<ParsedVoiceCommand> voiceCommandDeserializer(){
    JsonDeserializer<ParsedVoiceCommand> deserializer = new JsonDeserializer<>();
    return new ErrorHandlingDeserializer<>(deserializer);
  }
}
