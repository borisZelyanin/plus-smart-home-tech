package ru.practicum.collector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.practicum.collector.mapper.SensorEventMapper;
import ru.practicum.collector.model.sensor.*;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@Slf4j
@Service
@RequiredArgsConstructor
public class SensorEventServiceImpl implements SensorEventService {

    private final KafkaTemplate<String, SensorEventAvro> kafkaTemplate;
    private static final String TOPIC = "telemetry.sensors.v1";

    @Override
    public void send(LightSensorEvent event) {
        SensorEventAvro avro = SensorEventMapper.toAvro(event);
        kafkaTemplate.send(TOPIC, avro.getId(), avro);
        log.info("📤 LIGHT_SENSOR_EVENT отправлен: {}", avro);
    }

    @Override
    public void send(MotionSensorEvent event) {
        SensorEventAvro avro = SensorEventMapper.toAvro(event);
        kafkaTemplate.send(TOPIC, avro.getId(), avro);
        log.info("📤 MOTION_SENSOR_EVENT отправлен: {}", avro);
    }

    @Override
    public void send(ClimateSensorEvent event) {
        SensorEventAvro avro = SensorEventMapper.toAvro(event);
        kafkaTemplate.send(TOPIC, avro.getId(), avro);
        log.info("📤 CLIMATE_SENSOR_EVENT отправлен: {}", avro);
    }

    @Override
    public void send(SwitchSensorEvent event) {
        SensorEventAvro avro = SensorEventMapper.toAvro(event);
        kafkaTemplate.send(TOPIC, avro.getId(), avro);
        log.info("📤 SWITCH_SENSOR_EVENT отправлен: {}", avro);
    }

    @Override
    public void send(TemperatureSensorEvent event) {
        SensorEventAvro avro = SensorEventMapper.toAvro(event);
        kafkaTemplate.send(TOPIC, avro.getId(), avro);
        log.info("📤 TEMPERATURE_SENSOR_EVENT отправлен: {}", avro);
    }
}
