package ru.practicum.analyzer.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "conditions")
@Getter
@Setter
public class Condition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Тип условия: TEMPERATURE, MOTION и т.д.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConditionType type;

    // Операция сравнения: EQUALS, GREATER_THAN и т.д.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConditionOperation operation;

    // Значение, с которым сравниваем
    @Column(nullable = false)
    private Integer value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_id", nullable = false)
    private Sensor sensor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scenario_id", nullable = false)
    private Scenario scenario;
}