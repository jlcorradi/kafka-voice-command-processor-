package dev.jlcorradi.streams.voice;

import dev.jlcorradi.streams.voice.model.ParsedVoiceCommand;
import dev.jlcorradi.streams.voice.model.VoiceCommand;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;
import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DummyObjectProvider {

  public static final String EN_US = "en-US";
  public static final String FLAC = "FLAC";
  public static final String CALL_JOHN = "Call John";

  public static VoiceCommand voiceCommand(Function<VoiceCommand.VoiceCommandBuilder, VoiceCommand.VoiceCommandBuilder> x) {
    byte[] audio = new byte[20];
    VoiceCommand.VoiceCommandBuilder builder = VoiceCommand.builder()
        .id(UUID.randomUUID().toString())
        .audio(audio)
        .language(EN_US)
        .audioCodec(FLAC);
    builder = x.apply(builder);
    return builder.build();
  }

  public static ParsedVoiceCommand parsedVoiceCommand(Function<ParsedVoiceCommand.ParsedVoiceCommandBuilder, ParsedVoiceCommand.ParsedVoiceCommandBuilder> x) {
    ParsedVoiceCommand.ParsedVoiceCommandBuilder builder = ParsedVoiceCommand.builder()
        .id(UUID.randomUUID().toString())
        .audioCodec(FLAC)
        .language(EN_US)
        .text(CALL_JOHN)
        .probability(.98d);

    builder = x.apply(builder);
    return builder.build();
  }

}
