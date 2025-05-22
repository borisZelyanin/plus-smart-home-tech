package ru.practicum.collector.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.collector.model.sensor.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

@UtilityClass
public class SensorEventMapper {

    public SensorEventAvro toAvro(LightSensorEvent event) {
        LightSensorAvro payload = LightSensorAvro.newBuilder()
                .setLinkQuality(event.getLinkQuality())
                .setLuminosity(event.getLuminosity())
                .build();
        return buildSensorEvent(event.getId(), event.getHubId(), event.getTimestamp().toEpochMilli(), payload);
    }

    public SensorEventAvro toAvro(MotionSensorEvent event) {
        MotionSensorAvro payload = MotionSensorAvro.newBuilder()
                .setLinkQuality(event.getLinkQuality())
                .setMotion(event.isMotion())
                .setVoltage(event.getVoltage())
                .build();
        return buildSensorEvent(event.getId(), event.getHubId(), event.getTimestamp().toEpochMilli(), payload);
    }

    public SensorEventAvro toAvro(ClimateSensorEvent event) {
        ClimateSensorAvro payload = ClimateSensorAvro.newBuilder()
                .setTemperatureC(event.getTemperatureC())
                .setHumidity(event.getHumidity())
                .setCo2Level(event.getCo2Level())
                .build();
        return buildSensorEvent(event.getId(), event.getHubId(), event.getTimestamp().toEpochMilli(), payload);
    }

    public SensorEventAvro toAvro(SwitchSensorEvent event) {
        SwitchSensorAvro payload = SwitchSensorAvro.newBuilder()
                .setState(event.isState())
                .build();
        return buildSensorEvent(event.getId(), event.getHubId(), event.getTimestamp().toEpochMilli(), payload);
    }

    public SensorEventAvro toAvro(TemperatureSensorEvent event) {
        TemperatureSensorAvro payload = TemperatureSensorAvro.newBuilder()
                .setTemperatureC(event.getTemperatureC())
                .setTemperatureF(event.getTemperatureF())
                .build();
        return buildSensorEvent(event.getId(), event.getHubId(), event.getTimestamp().toEpochMilli(), payload);
    }

    private SensorEventAvro buildSensorEvent(String id, String hubId, long timestamp, Object payload) {
        return SensorEventAvro.newBuilder()
                .setId(id)
                .setHubId(hubId)
                .setTimestamp(timestamp)
                .setPayload(payload)
                .build();
    }
}
