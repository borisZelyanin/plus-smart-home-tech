package ru.practicum.analyzer.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {

    private final Consumer<String, HubEventAvro> consumer;

    private static final String HUB_EVENTS_TOPIC = "telemetry.hubs.v1";

    @Override
    public void run() {
        consumer.subscribe(List.of(HUB_EVENTS_TOPIC));
        log.info("✅ Подписка на топик с хаб-событиями: {}", HUB_EVENTS_TOPIC);

        try {
            while (true) {
                ConsumerRecords<String, HubEventAvro> records = consumer.poll(Duration.ofMillis(100));

                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    HubEventAvro event = record.value();
                    log.info("📦 Обработка события хаба: {}", event);
                    // TODO: обработка событий добавления/удаления устройств/сценариев
                }

                consumer.commitAsync();
            }
        } catch (WakeupException ignored) {
            log.info("🛑 Завершение обработки событий хаба");
        } catch (Exception e) {
            log.error("💥 Ошибка обработки событий хаба", e);
        } finally {
            try {
                consumer.commitSync();
            } finally {
                consumer.close();
                log.info("🧹 Consumer хаба закрыт");
            }
        }
    }
}