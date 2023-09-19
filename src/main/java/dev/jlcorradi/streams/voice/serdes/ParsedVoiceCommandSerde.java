package dev.jlcorradi.streams.voice.serdes;

import dev.jlcorradi.streams.voice.model.ParsedVoiceCommand;
import org.springframework.stereotype.Component;

@Component
public class ParsedVoiceCommandSerde extends JsonSerde<ParsedVoiceCommand> {
  public ParsedVoiceCommandSerde() {
    super(ParsedVoiceCommand.class);
  }
}
