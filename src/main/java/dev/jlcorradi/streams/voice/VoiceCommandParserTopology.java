package dev.jlcorradi.streams.voice;

import dev.jlcorradi.streams.voice.model.ParsedVoiceCommand;
import dev.jlcorradi.streams.voice.model.VoiceCommand;
import dev.jlcorradi.streams.voice.serdes.JsonSerde;
import dev.jlcorradi.streams.voice.services.SpeachToTextService;
import dev.jlcorradi.streams.voice.services.TranslateService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;

import java.util.Map;

@RequiredArgsConstructor
public class VoiceCommandParserTopology {

  public static final String VOICE_COMMANDS_TOPIC = "voice-commands";
  public static final String RECOGNIZED_COMMANDS_TOPIC = "recognized-commands";
  public static final String UNRECOGNIZED_COMMANDS_TOPIC = "unrecognized-commands";

  public final Double probabilityThreshold;
  private final SpeachToTextService speachToTextService;
  private final TranslateService translateService;

  public Topology createTopology() {
    StreamsBuilder streamsBuilder = new StreamsBuilder();

    Map<String, KStream<String, ParsedVoiceCommand>> branches = streamsBuilder.stream(VOICE_COMMANDS_TOPIC, Consumed.with(Serdes.String(), new JsonSerde<>(VoiceCommand.class)))
        //.filter((key, value) -> value.getAudio().length >= 10)
        .mapValues((readOnlyKey, value) -> speachToTextService.speechToText(value))
        .split(Named.as("branches-"))
        .branch((key, value) -> value.getProbability() >= probabilityThreshold, Branched.as("recognized"))
        .defaultBranch(Branched.as("unrecognized"));

    Map<String, KStream<String, ParsedVoiceCommand>> stringKStreamMap = branches.get("branches-recognized")
        .split(Named.as("language-"))
        .branch((key, value) -> value.getLanguage().startsWith("en"), Branched.as("english"))
        .defaultBranch(Branched.as("non-english"));

    JsonSerde<ParsedVoiceCommand> valueSerde = new JsonSerde<>(ParsedVoiceCommand.class);
    stringKStreamMap.get("language-non-english")
        .mapValues((readOnlyKey, value) -> translateService.translate(value))
        .merge(stringKStreamMap.get("language-english"))
        .to(RECOGNIZED_COMMANDS_TOPIC, Produced.with(Serdes.String(), valueSerde));

    branches.get("branches-unrecognized")
        .to(UNRECOGNIZED_COMMANDS_TOPIC, Produced.with(Serdes.String(), valueSerde));

    return streamsBuilder.build();
  }
}
