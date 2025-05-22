package ru.practicum.collector.model.sensor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@ToString(callSuper = true)
@Schema(description = "Показания датчика движения")
public class MotionSensorEvent extends SensorEvent {
    private int linkQuality;
    private boolean motion;
    private int voltage;

    public SensorEventType getType() {
        return SensorEventType.MOTION_SENSOR_EVENT;
    }
}
