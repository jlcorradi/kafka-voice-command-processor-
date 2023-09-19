package dev.jlcorradi.streams.voice;

import dev.jlcorradi.streams.voice.model.ParsedVoiceCommand;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static dev.jlcorradi.streams.voice.VoiceCommandParserTopology.RECOGNIZED_COMMANDS_TOPIC;
import static dev.jlcorradi.streams.voice.VoiceCommandParserTopology.UNRECOGNIZED_COMMANDS_TOPIC;

@Slf4j
@Component
public class ConsumerApplication {

  @KafkaListener(
      groupId = "application-listener",
      topics = {RECOGNIZED_COMMANDS_TOPIC, UNRECOGNIZED_COMMANDS_TOPIC},
      concurrency = "5")
  public void consume(ConsumerRecord<String, ParsedVoiceCommand> record) {
    log.info("Topic: {}: id: {} \n {}\n", record.topic(), record.key(), record.value());
  }

}
