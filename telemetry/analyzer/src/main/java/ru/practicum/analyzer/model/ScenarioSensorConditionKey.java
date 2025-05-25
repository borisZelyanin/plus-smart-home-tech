package ru.practicum.analyzer.model;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScenarioSensorConditionKey implements Serializable {

    private Long scenario;
    private String sensor;
    private Long condition;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScenarioSensorConditionKey)) return false;
        ScenarioSensorConditionKey that = (ScenarioSensorConditionKey) o;
        return Objects.equals(scenario, that.scenario) &&
                Objects.equals(sensor, that.sensor) &&
                Objects.equals(condition, that.condition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scenario, sensor, condition);
    }
}