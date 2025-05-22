package ru.practicum.collector.model.hub;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Schema(description = "Действие устройства в сценарии")
public class DeviceAction {
    private String sensorId;
    private ActionType type;
    private Integer value;
}
