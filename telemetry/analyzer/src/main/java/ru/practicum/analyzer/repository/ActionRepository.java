package ru.practicum.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.analyzer.model.Action;
import ru.practicum.analyzer.model.Scenario;

import java.util.List;

public interface ActionRepository extends JpaRepository<Action, Long> {
    void deleteByScenario(Scenario scenario);

    List<Action> findAllByScenarioIn(List<Scenario> scenarios);
}