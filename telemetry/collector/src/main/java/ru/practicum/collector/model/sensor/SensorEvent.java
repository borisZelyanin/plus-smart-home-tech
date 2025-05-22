package ru.practicum.collector.model.sensor;

import com.fasterxml.jackson.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.Instant;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = LightSensorEvent.class, name = "LIGHT_SENSOR_EVENT"),
        @JsonSubTypes.Type(value = MotionSensorEvent.class, name = "MOTION_SENSOR_EVENT"),
        @JsonSubTypes.Type(value = ClimateSensorEvent.class, name = "CLIMATE_SENSOR_EVENT"),
        @JsonSubTypes.Type(value = SwitchSensorEvent.class, name = "SWITCH_SENSOR_EVENT"),
        @JsonSubTypes.Type(value = TemperatureSensorEvent.class, name = "TEMPERATURE_SENSOR_EVENT")
})
@Getter
@Setter
@ToString
@Schema(discriminatorProperty = "type", oneOf = {
        LightSensorEvent.class,
        MotionSensorEvent.class,
        ClimateSensorEvent.class,
        SwitchSensorEvent.class,
        TemperatureSensorEvent.class
})
public abstract class SensorEvent {
    @Schema(description = "Идентификатор сенсора", example = "sensor.light.3")
    @NotBlank
    private String id;

    @Schema(description = "Идентификатор хаба", example = "hub-2")
    @NotBlank
    private String hubId;

    @Schema(description = "Метка времени события", example = "2024-08-06T16:54:03.129Z")
    private Instant timestamp = Instant.now();

    @NotNull
    @Schema(description = "Тип события сенсора", required = true)
    public abstract SensorEventType getType();
}
