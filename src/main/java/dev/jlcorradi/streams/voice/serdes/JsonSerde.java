package dev.jlcorradi.streams.voice.serdes;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

public class JsonSerde<T> implements Serde<T> {
  public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private final Class<T> clazz;

  public JsonSerde(Class<T> clazz) {
    this.clazz = clazz;
  }

  @Override
  public Serializer<T> serializer() {
    return this::serialize;
  }

  @Override
  public Deserializer<T> deserializer() {
    return (topic, data) -> deserialize(data);
  }

  @SneakyThrows
  private T deserialize(byte[] data) {
    return OBJECT_MAPPER.readValue(data, clazz);
  }

  @SneakyThrows
  private byte[] serialize(String topic, T data) {
    return OBJECT_MAPPER.writeValueAsBytes(data);
  }
}
