package ru.practicum.analyzer.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.analyzer.model.Action;
import ru.practicum.analyzer.model.Condition;
import ru.practicum.analyzer.model.Scenario;
import ru.practicum.analyzer.repository.ScenarioRepository;
import ru.practicum.analyzer.service.GrpcHubClient;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScenarioEvaluator {

    private final ScenarioRepository scenarioRepository;
    private final GrpcHubClient hubClient;

    public void evaluate(SensorsSnapshotAvro snapshot) {
        String hubId = snapshot.getHubId();
        Instant timestamp = snapshot.getTimestamp();
        Map<String, SensorStateAvro> sensorsState = snapshot.getSensorsState();

        List<Scenario> scenarios = scenarioRepository.findByHubId(hubId);
        log.info("🔎 Найдено сценариев для анализа: {}", scenarios.size());

        for (Scenario scenario : scenarios) {
            boolean allConditionsMet = scenario.getConditions().stream().allMatch(condition ->
                    evaluateCondition(condition, sensorsState.get(condition.getSensorId()))
            );

            if (allConditionsMet) {
                log.info("✅ Условия выполнены для сценария: {}", scenario.getName());
                for (Action action : scenario.getActions()) {
                    hubClient.sendAction(hubId, scenario.getName(), action, timestamp);
                }
            } else {
                log.debug("⛔ Сценарий не выполнен: {}", scenario.getName());
            }
        }
    }

    private boolean evaluateCondition(Condition condition, SensorStateAvro state) {
        if (state == null) {
            return false;
        }

        int actualValue;
        try {
            actualValue = switch (condition.getType()) {
                case "TEMPERATURE" -> state.getData().getTemperatureSensor().getValue();
                case "LUMINOSITY" -> state.getData().getLightSensor().getValue();
                case "CO2LEVEL" -> state.getData().getClimateSensor().getCo2();
                case "HUMIDITY" -> state.getData().getClimateSensor().getHumidity();
                case "MOTION" -> state.getData().getMotionSensor().getDetected() ? 1 : 0;
                case "SWITCH" -> state.getData().getSwitchSensor().getState() ? 1 : 0;
                default -> {
                    log.warn("⚠ Неизвестный тип условия: {}", condition.getType());
                    yield Integer.MIN_VALUE;
                }
            };
        } catch (Exception e) {
            log.warn("⚠ Ошибка при чтении значения датчика", e);
            return false;
        }

        return switch (condition.getOperation()) {
            case "EQUALS" -> actualValue == condition.getValue();
            case "GREATER_THAN" -> actualValue > condition.getValue();
            case "LOWER_THAN" -> actualValue < condition.getValue();
            default -> {
                log.warn("⚠ Неизвестная операция условия: {}", condition.getOperation());
                yield false;
            }
        };
    }
}