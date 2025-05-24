package ru.practicum.collector.handler.sensor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.collector.model.sensor.MotionSensorEvent;
import ru.practicum.collector.service.SensorEventService;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.time.Instant;

@Slf4j
@Component
public class MotionSensorEventHandler implements SensorEventHandler {

    private final SensorEventService sensorEventService;

    public MotionSensorEventHandler(SensorEventService sensorEventService) {
        this.sensorEventService = sensorEventService;
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.MOTION_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        var payload = event.getMotionSensorEventOrBuilder();

        MotionSensorEvent converted = MotionSensorEvent.builder()
                .linkQuality(payload.getLinkQuality())
                .motion(payload.getMotion())
                .voltage(payload.getVoltage())
                .build();

        converted.setId(event.getId());
        converted.setHubId(event.getHubId());
        converted.setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()));

        sensorEventService.send(converted);
        log.info("✅ MOTION_SENSOR: id={}, hubId={}", event.getId(), event.getHubId());
    }
}