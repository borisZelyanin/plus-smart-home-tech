package ru.practicum.analyzer.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.practicum.analyzer.logic.ScenarioEvaluator;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor {

    private final Consumer<String, SensorsSnapshotAvro> consumer;
    private final ScenarioEvaluator scenarioEvaluator;

    private static final String SNAPSHOTS_TOPIC = "telemetry.snapshots.v1";

    public void start() {
        consumer.subscribe(List.of(SNAPSHOTS_TOPIC));
        log.info("✅ Подписка на топик снапшотов: {}", SNAPSHOTS_TOPIC);

        try {
            while (true) {
                ConsumerRecords<String, SensorsSnapshotAvro> records = consumer.poll(Duration.ofMillis(100));

                for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                    SensorsSnapshotAvro snapshot = record.value();
                    log.info("🔍 Обработка снапшота: {}", snapshot.getHubId());

                    scenarioEvaluator.evaluate(snapshot);
                }

                consumer.commitAsync();
            }
        } catch (WakeupException ignored) {
            log.info("🛑 Завершение обработки снапшотов");
        } catch (Exception e) {
            log.error("💥 Ошибка обработки снапшотов", e);
        } finally {
            try {
                consumer.commitSync();
            } finally {
                consumer.close();
                log.info("🧹 Consumer снапшотов закрыт");
            }
        }
    }
}