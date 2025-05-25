package ru.practicum.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.analyzer.model.Action;

public interface ActionRepository extends JpaRepository<Action, Long> {
}