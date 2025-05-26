package ru.practicum.analyzer.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@ToString
@Table(name = "conditions")
@FieldDefaults(level = AccessLevel.PRIVATE)
@SecondaryTable(name = "scenario_conditions", pkJoinColumns = @PrimaryKeyJoinColumn(name = "condition_id"))
public class Condition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    ConditionType type;
    ConditionOperation operation;
    int value;

    @ManyToOne
    @JoinColumn(name = "scenario_id", table = "scenario_conditions")
    private Scenario scenario;

    @ManyToOne
    @JoinColumn(name = "sensor_id", table = "scenario_conditions")
    private Sensor sensor;
}