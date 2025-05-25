package ru.practicum.analyzer.model;

import java.io.Serializable;
import java.util.Objects;

public class ScenarioConditionId implements Serializable {
    private Long scenario;
    private String sensor;
    private Long condition;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScenarioConditionId that)) return false;
        return Objects.equals(scenario, that.scenario)
                && Objects.equals(sensor, that.sensor)
                && Objects.equals(condition, that.condition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scenario, sensor, condition);
    }
}