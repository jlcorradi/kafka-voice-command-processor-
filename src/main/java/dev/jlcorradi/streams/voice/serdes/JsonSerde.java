package dev.jlcorradi.streams.voice.serdes;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

public class JsonSerde<T> implements Serde<T> {
  public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private final Class<T> clazz;

  private JsonSerializer<T> serializer = new JsonSerializer<>();
  private JsonDeserializer<T> deSerializer = new JsonDeserializer<>();

  public JsonSerde(Class<T> clazz) {
    this.clazz = clazz;
  }

  @Override
  public Serializer<T> serializer() {
    return serializer;
  }

  @Override
  public Deserializer<T> deserializer() {
    return deSerializer;
  }

  class JsonSerializer<T> implements Serializer<T> {
    @Override
    @SneakyThrows
    public byte[] serialize(String topic, T data) {
      return OBJECT_MAPPER.writeValueAsBytes(data);
    }
  }

  class JsonDeserializer<T> implements Deserializer<T> {
    @Override
    @SneakyThrows
    public T deserialize(String topic, byte[] data) {
      return (T) OBJECT_MAPPER.readValue(data, clazz);
    }
  }
}
