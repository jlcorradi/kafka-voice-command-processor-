package dev.jlcorradi.streams.voice.serdes;

import dev.jlcorradi.streams.voice.model.VoiceCommand;
import org.springframework.stereotype.Component;

@Component
public class VoiceCommandSerde extends JsonSerde<VoiceCommand> {
  public VoiceCommandSerde() {
    super(VoiceCommand.class);
  }
}
