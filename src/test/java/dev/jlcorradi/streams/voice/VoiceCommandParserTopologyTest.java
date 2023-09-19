package dev.jlcorradi.streams.voice;

import dev.jlcorradi.streams.voice.model.ParsedVoiceCommand;
import dev.jlcorradi.streams.voice.model.VoiceCommand;
import dev.jlcorradi.streams.voice.serdes.JsonSerde;
import dev.jlcorradi.streams.voice.services.SpeachToTextService;
import dev.jlcorradi.streams.voice.services.TranslateService;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class VoiceCommandParserTopologyTest {

  public static final String CALL_JOHN = "Call John";
  @Mock
  private SpeachToTextService speachToTextService;
  @Mock
  private TranslateService translateService;

  private TestInputTopic<String, VoiceCommand> inputTopic;
  private TestOutputTopic<String, ParsedVoiceCommand> recognizedCommandsTopic;
  private TestOutputTopic<String, ParsedVoiceCommand> unrecognizedCommandsTopic;

  @BeforeEach
  void setup() {
    StreamsBuilder streamsBuilder = new StreamsBuilder();
    // Set the probability threshold
    VoiceCommandParserTopology voiceCommandParserTopology = new VoiceCommandParserTopology(
        0.98, // Set the probability threshold
        speachToTextService,
        translateService,
        streamsBuilder
    );

    voiceCommandParserTopology.process(streamsBuilder);

    TopologyTestDriver topologyTestDriver = new TopologyTestDriver(streamsBuilder.build());

    JsonSerde<VoiceCommand> voiceCommandJsonSerde = new JsonSerde<>(VoiceCommand.class);
    inputTopic = topologyTestDriver.createInputTopic(
        VoiceCommandParserTopology.VOICE_COMMANDS_TOPIC,
        Serdes.String().serializer(),
        voiceCommandJsonSerde.serializer());

    JsonSerde<ParsedVoiceCommand> parseVoiceCommandJsonSerde = new JsonSerde<>(ParsedVoiceCommand.class);
    recognizedCommandsTopic = topologyTestDriver.createOutputTopic(
        VoiceCommandParserTopology.RECOGNIZED_COMMANDS_TOPIC,
        Serdes.String().deserializer(),
        parseVoiceCommandJsonSerde.deserializer());

    unrecognizedCommandsTopic = topologyTestDriver.createOutputTopic(
        VoiceCommandParserTopology.UNRECOGNIZED_COMMANDS_TOPIC,
        Serdes.String().deserializer(),
        parseVoiceCommandJsonSerde.deserializer());
  }

  @Test
  void englishCommandProcess_successful() {
    // GIVEN
    VoiceCommand voiceCommand = DummyObjectProvider.voiceCommand(x -> x);
    ParsedVoiceCommand parsedVoiceCommand = DummyObjectProvider.parsedVoiceCommand(x -> x
        .id(voiceCommand.getId())
    );

    given(speachToTextService.speechToText(voiceCommand)).willReturn(parsedVoiceCommand);

    // WHEN
    inputTopic.pipeInput(voiceCommand);

    // THEN
    ParsedVoiceCommand output = recognizedCommandsTopic.readRecord().value();
    assertEquals(voiceCommand.getId(), output.getId());
    assertEquals(CALL_JOHN, output.getText());
  }

  @Test
  void nonEnglishCommandProcess_successful() {
    // GIVEN
    VoiceCommand voiceCommand = DummyObjectProvider.voiceCommand(x -> x);
    ParsedVoiceCommand parsedVoiceCommand = DummyObjectProvider.parsedVoiceCommand(x -> x
        .id(voiceCommand.getId())
        .language("pt-BR")
        .text("Ligar para o John")
    );

    given(speachToTextService.speechToText(voiceCommand)).willReturn(parsedVoiceCommand);
    given(translateService.translate(parsedVoiceCommand)).willReturn(
        DummyObjectProvider.parsedVoiceCommand(x -> x
            .id(voiceCommand.getId())
            .text(CALL_JOHN)
        ));

    // WHEN
    inputTopic.pipeInput(voiceCommand);

    // THEN
    ParsedVoiceCommand output = recognizedCommandsTopic.readRecord().value();
    assertEquals(parsedVoiceCommand.getId(), output.getId());
    assertEquals(CALL_JOHN, output.getText());
  }

  @Test
  void unrecognizableVoiceCommand_success() {
    // GIVEN
    VoiceCommand voiceCommand = DummyObjectProvider.voiceCommand(x -> x);

    ParsedVoiceCommand parsedVoiceCommand = DummyObjectProvider.parsedVoiceCommand(x -> x
        .id(voiceCommand.getId())
        .probability(.75d)
    );

    given(speachToTextService.speechToText(voiceCommand)).willReturn(parsedVoiceCommand);

    // WHEN
    inputTopic.pipeInput(voiceCommand);
    // THEN
    ParsedVoiceCommand output = unrecognizedCommandsTopic.readRecord().value();
    assertEquals(voiceCommand.getId(), output.getId());
    assertEquals(CALL_JOHN, output.getText());
  }

  @Test
  void shouldReceiveNoResponseForTooShortAudio_success() {
    // GIVEN
    VoiceCommand shortVoiceCommand = DummyObjectProvider.voiceCommand(x -> x.audio(new byte[9]));

    // WHEN
    inputTopic.pipeInput(shortVoiceCommand);

    // THEN
    assertTrue(recognizedCommandsTopic.isEmpty());
    assertTrue(recognizedCommandsTopic.isEmpty());
    verify(speachToTextService, never()).speechToText(shortVoiceCommand);
  }

}