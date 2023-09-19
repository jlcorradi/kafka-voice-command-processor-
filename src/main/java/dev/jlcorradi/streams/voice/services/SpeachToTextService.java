package dev.jlcorradi.streams.voice.services;

import dev.jlcorradi.streams.voice.model.ParsedVoiceCommand;
import dev.jlcorradi.streams.voice.model.VoiceCommand;

public interface SpeachToTextService {
  ParsedVoiceCommand speechToText(VoiceCommand original);
}
