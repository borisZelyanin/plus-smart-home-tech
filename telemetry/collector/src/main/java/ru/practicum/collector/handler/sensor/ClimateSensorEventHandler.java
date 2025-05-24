package ru.practicum.collector.handler.sensor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.collector.model.sensor.ClimateSensorEvent;
import ru.practicum.collector.service.SensorEventService;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.time.Instant;

@Slf4j
@Component
public class ClimateSensorEventHandler implements SensorEventHandler {

    private final SensorEventService sensorEventService;

    public ClimateSensorEventHandler(SensorEventService sensorEventService) {
        this.sensorEventService = sensorEventService;
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.CLIMATE_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        var payload = event.getClimateSensorEvent();

        ClimateSensorEvent converted = ClimateSensorEvent.builder()
                .temperatureC(payload.getTemperatureC())
                .humidity(payload.getHumidity())
                .co2Level(payload.getCo2Level())
                .build();

        converted.setId(event.getId());
        converted.setHubId(event.getHubId());
        converted.setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()));

        sensorEventService.send(converted);
        log.info("✅ CLIMATE_SENSOR: id={}, hubId={}", event.getId(), event.getHubId());
    }
}