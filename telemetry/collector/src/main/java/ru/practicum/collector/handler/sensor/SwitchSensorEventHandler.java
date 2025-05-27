package ru.practicum.collector.handler.sensor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.collector.model.sensor.SwitchSensorEvent;
import ru.practicum.collector.service.SensorEventService;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.time.Instant;


@Slf4j
@Component
public class SwitchSensorEventHandler implements SensorEventHandler {

    private final SensorEventService sensorEventService;

    public SwitchSensorEventHandler(SensorEventService sensorEventService) {
        this.sensorEventService = sensorEventService;
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.SWITCH_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        var payload = event.getSwitchSensorEvent();

        SwitchSensorEvent converted = SwitchSensorEvent.builder()
                .state(payload.getState())
                .build();

        converted.setId(event.getId());
        converted.setHubId(event.getHubId());
        converted.setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()));

        sensorEventService.send(converted);
        log.info("✅ SWITCH_SENSOR: id={}, hubId={}", event.getId(), event.getHubId());
    }
}