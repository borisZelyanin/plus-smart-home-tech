package ru.practicum.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.analyzer.model.Condition;
import ru.practicum.analyzer.model.Scenario;

import java.util.List;

public interface ConditionRepository extends JpaRepository<Condition, Long> {
    void deleteByScenario(Scenario scenario);

    List<Condition> findAllByScenarioHubId(String hubId);
}