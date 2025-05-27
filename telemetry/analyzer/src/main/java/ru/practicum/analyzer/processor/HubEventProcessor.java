package ru.practicum.analyzer.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.practicum.analyzer.config.AnalyzerProperties;
import ru.practicum.analyzer.logic.HubRegistry;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Duration;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {

    private final Consumer<String, HubEventAvro> consumer;
    private final AnalyzerProperties props;
    private final HubRegistry handleEvent;
    private static final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    @Override
    public void run() {
        log.info("🚀 Запуск HubEventProcessor...");

        // Подписка на топик
        String topic = props.getHubTopic();
        consumer.subscribe(List.of(topic));
        log.info("✅ Подписка на топик с хаб-событиями: {}", topic);

        try {
            while (true) {
                log.debug("📡 Ожидание новых сообщений из Kafka...");

                ConsumerRecords<String, HubEventAvro> records = consumer.poll(Duration.ofMillis(2000));
                int count = records.count();
                log.debug("📥 Получено {} записей из Kafka", count);

                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    HubEventAvro event = record.value();
                    log.debug("📦 Получен event: {}", record);
                    log.debug("🔍 Обработка события от хаба '{}': ключ='{}', offset={}, partition={}",
                            event.getHubId(), record.key(), record.offset(), record.partition());
                    handleEvent.handle(event);
                    log.debug("💾 Асинхронный коммит offset'ов...");
                    manageOffsets(record, consumer);
                }


            }
        } catch (WakeupException ignored) {
            log.info("🛑 Завершение обработки событий хаба по сигналу Wakeup");
        } catch (Exception e) {
            log.error("💥 Необработанная ошибка во время обработки событий", e);
        } finally {
            try {
                log.info("💾 Финальный синхронный коммит offset'ов...");
                consumer.commitSync();
            } catch (Exception e) {
                log.warn("⚠ Ошибка при финальном коммите offset'ов", e);
            } finally {
                consumer.close();
                log.info("🧹 Consumer хаба закрыт");
            }
        }
    }
    private static void manageOffsets(ConsumerRecord<String, HubEventAvro> record,
                                      Consumer<String, HubEventAvro> consumer) {
        currentOffsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1)
        );
    }
}