package ru.practicum.analyzer.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.analyzer.model.*;
import ru.practicum.analyzer.repository.ScenarioRepository;
import ru.practicum.analyzer.service.GrpcHubClient;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

        log.debug("📥 Получен снапшот: hubId={}, timestamp={}, sensors={}",
                hubId, timestamp, sensorsState.keySet());

        List<Scenario> scenarios = scenarioRepository.findByHubId(hubId);
        if (scenarios.isEmpty()) {
            log.info("📭 Нет сценариев для хаба: {}", hubId);
            return;
        }

        log.info("🔎 Анализ сценариев: найдено {} для хаба {}", scenarios.size(), hubId);

        for (Scenario scenario : scenarios) {
            log.debug("🧪 Проверка сценария: '{}'", scenario.getName());

            boolean allConditionsMet = scenario.getConditions().stream().allMatch(condition -> {
                String sensorId = condition.getSensor().getId();
                SensorStateAvro state = sensorsState.get(sensorId);
                log.debug("🔍 Условие по сенсору: {}", sensorId);
                return evaluateCondition(condition.getCondition(), state);
            });

            if (allConditionsMet) {
                log.info("✅ Все условия выполнены: сценарий '{}'", scenario.getName());
                for (ScenarioAction scenarioAction : scenario.getActions()) {
                    hubClient.sendAction(hubId, scenario.getName(), scenarioAction.getAction(), timestamp);
                }
            } else {
                log.debug("⛔ Условия не выполнены: сценарий '{}'", scenario.getName());
            }
        }
    }

    private boolean evaluateCondition(Condition condition, SensorStateAvro state) {
        if (state == null) return false;
        Objects.requireNonNull(condition.getValue(), "Condition value must not be null");

        Integer actualValue;
        try {
            ConditionType type = ConditionType.valueOf(condition.getType().name());
            switch (type) {
                case TEMPERATURE -> {
                    var temp = extractSensorData(state, TemperatureSensorAvro.class);
                    actualValue = temp != null ? temp.getTemperatureC() : null;
                }
                case LUMINOSITY -> {
                    var light = extractSensorData(state, LightSensorAvro.class);
                    actualValue = light != null ? light.getLuminosity() : null;
                }
                case CO2LEVEL -> {
                    var climate = extractSensorData(state, ClimateSensorAvro.class);
                    actualValue = climate != null ? climate.getCo2Level() : null;
                }
                case HUMIDITY -> {
                    var climate = extractSensorData(state, ClimateSensorAvro.class);
                    actualValue = climate != null ? climate.getHumidity() : null;
                }
                case MOTION -> {
                    var motion = extractSensorData(state, MotionSensorAvro.class);
                    actualValue = motion != null ? (motion.getMotion() ? 1 : 0) : null;
                }
                case SWITCH -> {
                    var sw = extractSensorData(state, SwitchSensorAvro.class);
                    actualValue = sw != null ? (sw.getState() ? 1 : 0) : null;
                }
                default -> {
                    log.warn("⚠ Необработанный тип условия: {}", type);
                    return false;
                }
            }

            if (actualValue == null) {
                log.warn("⚠ Не получено значение сенсора для условия: {}", condition);
                return false;
            }

        } catch (Exception e) {
            log.warn("⚠ Ошибка при обработке условия", e);
            return false;
        }

        log.debug("📐 Сравнение: фактическое={} ожидаемое={} операция={}",
                actualValue, condition.getValue(), condition.getOperation());

        return switch (condition.getOperation().name()) {
            case "EQUALS" -> actualValue.equals(condition.getValue());
            case "GREATER_THAN" -> actualValue > condition.getValue();
            case "LOWER_THAN" -> actualValue < condition.getValue();
            default -> {
                log.warn("⚠ Неизвестная операция: {}", condition.getOperation());
                yield false;
            }
        };
    }

    @SuppressWarnings("unchecked")
    private <T> T extractSensorData(SensorStateAvro state, Class<T> expectedClass) {
        Object payload = state.getData();
        if (expectedClass.isInstance(payload)) {
            return (T) payload;
        } else {
            log.warn("⚠ Тип данных не совпадает: ожидался {}, получен {}",
                    expectedClass.getSimpleName(), payload.getClass().getSimpleName());
            return null;
        }
    }
}