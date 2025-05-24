package ru.practicum.collector.handler.sensor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.collector.model.sensor.TemperatureSensorEvent;
import ru.practicum.collector.service.SensorEventService;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.TemperatureSensorEventOrBuilder;

import java.time.Instant;

@Slf4j
@Component
public class TemperatureSensorEventHandler implements SensorEventHandler {

    private final SensorEventService sensorEventService;

    public TemperatureSensorEventHandler(SensorEventService sensorEventService) {
        this.sensorEventService = sensorEventService;
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.TEMPERATURE_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        var payload = event.getTemperatureSensorEvent();

        TemperatureSensorEvent converted = TemperatureSensorEvent.builder()
                .temperatureC(payload.getTemperatureC())
                .temperatureF(payload.getTemperatureF())
                .build();

        converted.setId(event.getId());
        converted.setHubId(event.getHubId());
        converted.setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()));

        sensorEventService.send(converted);
        log.info("✅ TEMPERATURE_SENSOR: id={}, hubId={}", event.getId(), event.getHubId());
    }
}