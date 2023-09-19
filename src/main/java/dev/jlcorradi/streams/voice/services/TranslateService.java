package dev.jlcorradi.streams.voice.services;

import dev.jlcorradi.streams.voice.model.ParsedVoiceCommand;

public interface TranslateService {
  ParsedVoiceCommand translate(ParsedVoiceCommand command);
}
