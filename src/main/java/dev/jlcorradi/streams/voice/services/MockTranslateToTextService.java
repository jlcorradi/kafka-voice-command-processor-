package dev.jlcorradi.streams.voice.services;

import dev.jlcorradi.streams.voice.model.ParsedVoiceCommand;
import org.springframework.stereotype.Service;

@Service
public class MockTranslateToTextService implements TranslateService {
  @Override
  public ParsedVoiceCommand translate(ParsedVoiceCommand command) {
    return ParsedVoiceCommand.builder()
        .id(command.getId())
        .text("Call John")
        .probability(command.getProbability())
        .audioCodec(command.getAudioCodec())
        .build();

  }
}
