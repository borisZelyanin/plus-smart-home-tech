package ru.practicum.collector.handler.sensor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.LightSensorEventOrBuilder;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;


@Slf4j
@Component
public class LightSensorEventHandler implements SensorEventHandler {

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        LightSensorEventOrBuilder payload = event.getLightSensorEventOrBuilder();
        log.info("✅ LIGHT_SENSOR: id={}, hubId={}", event.getId(), event.getHubId());
    }
}
