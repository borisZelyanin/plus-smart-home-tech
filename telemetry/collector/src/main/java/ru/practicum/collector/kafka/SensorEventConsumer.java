package ru.practicum.collector.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@Slf4j
@Component
public class SensorEventConsumer {

    @KafkaListener(topics = "telemetry.sensors.v1", groupId = "collector-sensor-group",
            containerFactory = "sensorKafkaListenerContainerFactory")
    public void listen(SensorEventAvro event) {
        log.info("📥 Получено событие от сенсора: {}", event);
    }
}
