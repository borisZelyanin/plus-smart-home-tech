package ru.practicum.collector.model.sensor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@ToString(callSuper = true)
@Schema(description = "Показания датчика освещенности")
public class LightSensorEvent extends SensorEvent {
    private int linkQuality;
    private int luminosity;

    public SensorEventType getType() {
        return SensorEventType.LIGHT_SENSOR_EVENT;
    }
}
