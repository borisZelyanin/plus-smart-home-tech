package ru.practicum.collector.handler.sensor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.collector.model.sensor.LightSensorEvent;
import ru.yandex.practicum.grpc.telemetry.event.LightSensorEventOrBuilder;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.practicum.collector.service.SensorEventService;


@Slf4j
@Component
public class LightSensorEventHandler implements SensorEventHandler {

    private final SensorEventService sensorEventService;

    public LightSensorEventHandler(SensorEventService sensorEventService) {
        this.sensorEventService = sensorEventService;
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        LightSensorEventOrBuilder payload = event.getLightSensorEventOrBuilder();

        LightSensorEvent converted = LightSensorEvent.builder()

                .luminosity(payload.getLuminosity())
                .linkQuality(payload.getLinkQuality())
                .build();

        converted.setId(event.getId());
        converted.setHubId(event.getHubId());
        converted.setTimestamp(java.time.Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()));

        sensorEventService.send(converted);
        log.info("✅ LIGHT_SENSOR: id={}, hubId={}", event.getId(), event.getHubId());
    }
}
