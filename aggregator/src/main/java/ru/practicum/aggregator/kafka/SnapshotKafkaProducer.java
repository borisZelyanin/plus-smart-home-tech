package ru.practicum.aggregator.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;


@Component
@RequiredArgsConstructor
public class SnapshotKafkaProducer {

    private final KafkaProducer<String, SensorsSnapshotAvro> producer;

    public void send(SensorsSnapshotAvro snapshot) {
        ProducerRecord<String, SensorsSnapshotAvro> record =
                new ProducerRecord<>("telemetry.snapshots.v1", snapshot.getHubId(), snapshot);
        producer.send(record);
    }

    public void close() {
        producer.flush();
        producer.close();
    }
}