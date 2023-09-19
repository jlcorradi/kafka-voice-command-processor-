package dev.jlcorradi.streams.voice.services;

import dev.jlcorradi.streams.voice.model.ParsedVoiceCommand;
import dev.jlcorradi.streams.voice.model.VoiceCommand;

public class MockSpeachToTextService implements SpeachToTextService {

  @Override
  public ParsedVoiceCommand speechToText(VoiceCommand original) {
    final ParsedVoiceCommand.ParsedVoiceCommandBuilder builder = ParsedVoiceCommand.builder();
    builder
        .id(original.getId())
        .language(original.getLanguage())
        .audioCodec(original.getAudioCodec());

    switch (original.getId()) {
      case "f47ac10b-58cc-4372-a567-0e02b2c3d479" -> builder.text("Call John").probability(.987);
      case "6ba7b810-9dad-11d1-80b4-00c04fd430c8" -> builder.text("Ligar para John").probability(.937);
      case "38400000-8cf0-11bd-b23e-10b96e4ef00d" -> builder.text("Call John").probability(.37);
    }

    return builder.build();

  }

}
