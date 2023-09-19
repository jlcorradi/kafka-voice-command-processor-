package dev.jlcorradi.streams.voice.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParsedVoiceCommand {
  private String id;
  private String text;
  private String audioCodec;
  private String language;
  private Double probability;
}
