package ru.practicum.collector.handler.sensor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.TemperatureSensorEventOrBuilder;

@Slf4j
@Component
public class TemperatureSensorEventHandler implements SensorEventHandler {

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.TEMPERATURE_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        TemperatureSensorEventOrBuilder payload = event.getTemperatureSensorEventOrBuilder();
        log.info("✅ TEMPERATURE_SENSOR: id={}, hubId={}", event.getId(), event.getHubId());
    }
}
