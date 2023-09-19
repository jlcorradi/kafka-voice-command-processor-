package dev.jlcorradi.streams.voice;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.jlcorradi.streams.voice.model.VoiceCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
//@Component
public class Producer {

  private final KafkaTemplate<String, VoiceCommand> kafkaTemplate;
  static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  public void sendPayload() {
    List<VoiceCommand> voiceCommands = readTestData();
    voiceCommands.stream()
        .forEach(command -> kafkaTemplate.send(command.getId(), command));
  }

  private static List<VoiceCommand> readTestData() {
    List<VoiceCommand> commands = new ArrayList<>();
    try {
      InputStream inputStream = Producer.class.getClassLoader().getResourceAsStream("test-data.json");

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
