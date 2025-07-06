package ru.practicum.aggregator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.practicum.aggregator.config.AggregatorProperties;
import ru.practicum.aggregator.logic.SnapshotAggregator;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class AggregationStarter {

    private final Consumer<String, SensorEventAvro> consumer;
    private final Producer<String, SensorsSnapshotAvro> producer;
    private final SnapshotAggregator aggregator;
    private final AggregatorProperties props; // <--- добавили

    public void start() {
        consumer.subscribe(List.of(props.getInputTopic())); // было SOURCE_TOPIC

        log.info("📥 Подписка на топик: {}", props.getInputTopic());

        try {
            while (true) {
                var records = consumer.poll(Duration.ofMillis(2000));

                for (var record : records) {
                    var event = record.value();
                    log.debug("➡ Получено событие: {}", event);

                    aggregator.updateState(event).ifPresent(snapshot -> {
                        var snapshotRecord =
                                new ProducerRecord<>(props.getOutputTopic(), snapshot.getHubId(), snapshot);

                        producer.send(snapshotRecord, (metadata, ex) -> {
                            if (ex != null) {
                                log.error("❌ Ошибка при отправке снапшота в Kafka", ex);
                            } else {
                                log.info("✅ Снапшот отправлен: hubId={}, partition={}, offset={}",
                                        snapshot.getHubId(), metadata.partition(), metadata.offset());
                            }
                        });
                    });
                }

                consumer.commitAsync();
            }

        } catch (WakeupException ignored) {
            log.info("⛔ Получен сигнал завершения");
        } catch (Exception e) {
            log.error("💥 Ошибка в процессе агрегации", e);
        } finally {
            try {
                consumer.commitSync();
            } catch (Exception e) {
                log.warn("⚠ Ошибка при синхронной фиксации offset", e);
            } finally {
                try {
                    consumer.close();
                    log.info("📴 Consumer закрыт");
                } catch (Exception e) {
                    log.warn("⚠ Ошибка при закрытии consumer", e);
                }
                try {
                    producer.flush();
                    producer.close();
                    log.info("📤 Producer закрыт");
                } catch (Exception e) {
                    log.warn("⚠ Ошибка при закрытии producer", e);
                }
            }
        }
    }
}