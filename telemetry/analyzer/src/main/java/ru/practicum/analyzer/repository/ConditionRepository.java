package ru.practicum.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.analyzer.model.Condition;

public interface ConditionRepository extends JpaRepository<Condition, Long> {
}