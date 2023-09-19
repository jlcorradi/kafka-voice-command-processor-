package dev.jlcorradi.streams.voice;

import dev.jlcorradi.streams.voice.services.MockSpeachToTextService;
import dev.jlcorradi.streams.voice.services.MockTranslateToTextService;
import dev.jlcorradi.streams.voice.services.SpeachToTextService;
import dev.jlcorradi.streams.voice.services.TranslateService;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.Topology;

import java.util.Properties;

public class VoiceCommandParserApp {
  public static void main(String[] args) {
    Properties props = StreamsConfig.createConfig();

    SpeachToTextService speachToTextService = new MockSpeachToTextService();
    TranslateService translateService = new MockTranslateToTextService();

    VoiceCommandParserTopology voiceCommandParserTopology =
        new VoiceCommandParserTopology(.80d, speachToTextService, translateService);
    Topology topology = voiceCommandParserTopology.createTopology();
    KafkaStreams kafkaStreams = new KafkaStreams(topology, props);

    kafkaStreams.start();

    Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));
  }
}