package ru.practicum.collector.service;

import ru.practicum.collector.model.sensor.*;

public interface SensorEventService {
    void send(LightSensorEvent event);

    void send(MotionSensorEvent event);

    void send(ClimateSensorEvent event);

    void send(SwitchSensorEvent event);

    void send(TemperatureSensorEvent event);
}
