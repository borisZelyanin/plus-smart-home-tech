package ru.practicum.collector.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.collector.model.sensor.*;
import ru.practicum.collector.service.SensorEventService;

@RestController
@RequestMapping("/events/sensors")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Sensor Events", description = "API для обработки событий от сенсоров")
public class SensorEventController {

    private final SensorEventService sensorEventService;

    @PostMapping
    @Operation(summary = "Обработка событий от сенсоров")
    public void collectSensorEvent(@Valid @RequestBody SensorEvent event) {
        log.info("Получено событие от сенсора: {}", event);

        switch (event.getType()) {
            case LIGHT_SENSOR_EVENT -> sensorEventService.send((LightSensorEvent) event);
            case MOTION_SENSOR_EVENT -> sensorEventService.send((MotionSensorEvent) event);
            case CLIMATE_SENSOR_EVENT -> sensorEventService.send((ClimateSensorEvent) event);
            case SWITCH_SENSOR_EVENT -> sensorEventService.send((SwitchSensorEvent) event);
            case TEMPERATURE_SENSOR_EVENT -> sensorEventService.send((TemperatureSensorEvent) event);
            default -> throw new IllegalArgumentException("Неизвестный тип события: " + event.getType());
        }
    }
}
