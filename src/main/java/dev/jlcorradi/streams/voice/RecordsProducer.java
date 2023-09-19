package dev.jlcorradi.streams.voice;

import dev.jlcorradi.streams.voice.model.VoiceCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Component
public class RecordsProducer implements CommandLineRunner {

  @Autowired
  private final KafkaTemplate<String, VoiceCommand> kafkaTemplate;

  @Override
  public void run(String... args) throws Exception {
    VoiceCommand command = VoiceCommand.builder()
        .id("6ba7b810-9dad-11d1-80b4-00c04fd430c8")
        .audioCodec("FAC")
        .language("en-US")
        .build();

    CompletableFuture<SendResult<String, VoiceCommand>> sendOperation = kafkaTemplate.send(
        VoiceCommandParserTopology.VOICE_COMMANDS_TOPIC,
        command
    );
    sendOperation.get();
  }

}
