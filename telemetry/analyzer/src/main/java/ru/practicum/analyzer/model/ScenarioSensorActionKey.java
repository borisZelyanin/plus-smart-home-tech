package ru.practicum.analyzer.model;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScenarioSensorActionKey implements Serializable {

    private Long scenario;
    private String sensor;
    private Long action;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScenarioSensorActionKey)) return false;
        ScenarioSensorActionKey that = (ScenarioSensorActionKey) o;
        return Objects.equals(scenario, that.scenario) &&
                Objects.equals(sensor, that.sensor) &&
                Objects.equals(action, that.action);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scenario, sensor, action);
    }
}