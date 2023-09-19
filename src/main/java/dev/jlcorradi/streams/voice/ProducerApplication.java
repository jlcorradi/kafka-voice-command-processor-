package dev.jlcorradi.streams.voice;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.jlcorradi.streams.voice.config.StreamsProperties;
import dev.jlcorradi.streams.voice.model.VoiceCommand;
import dev.jlcorradi.streams.voice.serdes.JsonSerde;
import lombok.SneakyThrows;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.Serdes;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class ProducerApplication {

  static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  public static void main(String... args) {
    JsonSerde<VoiceCommand> voiceCommandJsonSerde = new JsonSerde<>(VoiceCommand.class);
    KafkaProducer<String, VoiceCommand> voiceCommandProducer =
        new KafkaProducer<>(new StreamsProperties().createConfig(), Serdes.String().serializer(), voiceCommandJsonSerde.serializer());

    List<VoiceCommand> voiceCommands = readTestData();
    voiceCommands.stream()
        .map(command -> new ProducerRecord<>(VoiceCommandParserTopology.VOICE_COMMANDS_TOPIC, command.getId(), command))
        .map(voiceCommandProducer::send)
        .forEach(ProducerApplication::waitForProducer);
  }

  @SneakyThrows
  private static void waitForProducer(Future<RecordMetadata> recordMetadataFuture) {
    recordMetadataFuture.get();
  }

  private static List<VoiceCommand> readTestData() {
    List<VoiceCommand> commands = new ArrayList<>();
    try {
      InputStream inputStream = ProducerApplication.class.getClassLoader().getResourceAsStream("test-data.json");

      if (inputStream != null) {
        commands = OBJECT_MAPPER.readValue(inputStream, new TypeReference<>() {
        });

      } else {
        System.err.println("JSON file not found in resources.");
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return commands;
  }
}
