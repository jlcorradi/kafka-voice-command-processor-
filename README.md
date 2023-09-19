# Kafka Voice Command Processor
This is a simple application created as a test scenario example for Kafka Streams.

## Requirements
Java 17 or later
Kafka and Zookeeper running 

## Topology

~~~
This is supposed to be source code
~~~

### Kafka Topics
Creating the required topics:
~~~~
# Run against zookeeper
kafka-topics --bootstrap-server kafka:9092 \
  --topic voice-commands \
  --replication-factor 1 \
  --partitions 10 --create

kafka-topics --bootstrap-server kafka:9092 \
  --topic recognized-commands \
  --replication-factor 1 \
  --partitions 10 --create

kafka-topics --bootstrap-server kafka:9092 \
  --topic unrecognized-commands \
  --replication-factor 1 \
  --partitions 10 --create

~~~~