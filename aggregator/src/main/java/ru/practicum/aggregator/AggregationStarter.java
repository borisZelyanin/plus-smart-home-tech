package ru.practicum.aggregator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.stereotype.Component;
import ru.practicum.aggregator.core.SnapshotAggregator;
import ru.practicum.aggregator.kafka.SnapshotKafkaProducer;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;


import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {

    private final Consumer<String, SensorEventAvro> consumer;
    private final SnapshotKafkaProducer snapshotProducer;
    private final SnapshotAggregator aggregator;

    public void start() {
        consumer.subscribe(java.util.List.of("telemetry.sensors.v1"));
        try {
            while (true) {
                ConsumerRecords<String, SensorEventAvro> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, SensorEventAvro> record : records) {
                    SensorEventAvro event = record.value();
                    aggregator.updateSnapshot(event).ifPresent(snapshot -> {
                        snapshotProducer.send(snapshot);
                    });
                }
                consumer.commitSync();
            }
        } catch (Exception e) {
            log.error("Ошибка агрегации", e);
        } finally {
            consumer.close();
            snapshotProducer.close();
        }
    }
}