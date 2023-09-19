package dev.jlcorradi.streams.voice.web;

import dev.jlcorradi.streams.voice.VoiceCommandParserTopology;
import dev.jlcorradi.streams.voice.model.VoiceCommand;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/voice-commands")
public class VoiceCommandApi {

  public final KafkaTemplate<String, VoiceCommand> kafkaTemplate;

  @PostMapping
  public void sendVoiceCommands(@RequestBody List<VoiceCommand> voiceCommands) {
    voiceCommands.stream()
        .map(command -> kafkaTemplate.send(VoiceCommandParserTopology.VOICE_COMMANDS_TOPIC, command))
        .forEach(this::waitProducer);
  }

  @SneakyThrows
  private void waitProducer(CompletableFuture<SendResult<String, VoiceCommand>> sendResultCompletableFuture) {
    sendResultCompletableFuture.get();
  }
}
