package dev.jlcorradi.streams.voice.serdes;

import dev.jlcorradi.streams.voice.model.VoiceCommand;
import lombok.SneakyThrows;
import org.apache.kafka.common.serialization.Serializer;

import static dev.jlcorradi.streams.voice.serdes.JsonSerde.OBJECT_MAPPER;

public class VoiceCommandSerializer implements Serializer<VoiceCommand> {
  @Override
  @SneakyThrows
  public byte[] serialize(String topic, VoiceCommand data) {
    return OBJECT_MAPPER.writeValueAsBytes(data);
  }
}
