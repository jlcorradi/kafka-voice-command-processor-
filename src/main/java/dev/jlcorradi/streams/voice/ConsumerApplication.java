package dev.jlcorradi.streams.voice;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.jlcorradi.streams.voice.config.StreamsProperties;
import dev.jlcorradi.streams.voice.model.ParsedVoiceCommand;
import dev.jlcorradi.streams.voice.serdes.JsonSerde;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.Serdes;

import java.time.Duration;
import java.util.List;

import static dev.jlcorradi.streams.voice.VoiceCommandParserTopology.RECOGNIZED_COMMANDS_TOPIC;
import static dev.jlcorradi.streams.voice.VoiceCommandParserTopology.UNRECOGNIZED_COMMANDS_TOPIC;

@Slf4j
public class ConsumerApplication {
  static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  public static void main(String[] args) {
    JsonSerde<ParsedVoiceCommand> voiceCommandJsonSerde = new JsonSerde<>(ParsedVoiceCommand.class);

    try (KafkaConsumer<String, ParsedVoiceCommand> consumer =
             new KafkaConsumer<>(new StreamsProperties().createConfig(), Serdes.String().deserializer(), voiceCommandJsonSerde.deserializer())) {

      Runtime.getRuntime().addShutdownHook(new Thread(consumer::close));
      consumer.subscribe(List.of(RECOGNIZED_COMMANDS_TOPIC, UNRECOGNIZED_COMMANDS_TOPIC));

      while (true) {
        consumer.poll(Duration.ofSeconds(1l))
            .forEach(record -> log.info("Topic: {}: id: {} \n {}\n", record.topic(), record.key(), record.value()));
        consumer.commitAsync();
      }

    }
  }
}
