package ru.practicum.analyzer.logic;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.analyzer.model.*;
import ru.practicum.analyzer.repository.ActionRepository;
import ru.practicum.analyzer.repository.ConditionRepository;
import ru.practicum.analyzer.repository.ScenarioRepository;
import ru.practicum.analyzer.repository.SensorRepository;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class HubRegistry {
    private final SensorRepository sensorRepository;
    private final ScenarioRepository scenarioRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;

    public void handle(HubEventAvro event) {
        Object payload = event.getPayload();
        String hubId = event.getHubId();

        if (payload instanceof DeviceAddedEventAvro deviceAdded) {
            handleDeviceAdded(hubId, deviceAdded);

        } else if (payload instanceof DeviceRemovedEventAvro deviceRemoved) {
            handleDeviceRemoved(hubId, deviceRemoved);

        } else if (payload instanceof ScenarioAddedEventAvro scenarioAdded) {
            handleScenarioAdded(hubId, scenarioAdded);

        } else if (payload instanceof ScenarioRemovedEventAvro scenarioRemoved) {
            handleScenarioRemoved(hubId, scenarioRemoved);

        } else {
            log.warn("⚠ Неизвестный тип события от хаба '{}': {}", hubId, payload.getClass().getSimpleName());
        }
    }

    private void handleDeviceAdded(String hubId, DeviceAddedEventAvro event) {
        String id = event.getId();
        log.info("📦 Добавление устройства '{}' для хаба '{}'", id, hubId);

        if (!sensorRepository.existsByIdInAndHubId(List.of(id), hubId)) {
            Sensor sensor = new Sensor();
            sensor.setId(id);
            sensor.setHubId(hubId);
            sensorRepository.save(sensor);
            sensorRepository.flush();
            log.info("✅ Устройство сохранено: {}", sensor);
        } else {
            log.info("🔁 Устройство '{}' уже существует для хаба '{}'", id, hubId);
        }
    }

    private void handleDeviceRemoved(String hubId, DeviceRemovedEventAvro event) {
        String id = event.getId();
        log.info("🗑 Удаление устройства '{}' из хаба '{}'", id, hubId);
        sensorRepository.deleteById(id);
        log.info("✅ Устройство удалено: {}", id);
    }

    private void handleScenarioAdded(String hubId, ScenarioAddedEventAvro event) {
        String name = event.getName();
        log.info("📝 Добавление/обновление сценария '{}' для хаба '{}'", name, hubId);
        Optional<Scenario> existing = scenarioRepository.findByHubIdAndName(hubId, name);

        try {
            if (existing.isPresent()) {
                updateScenario(existing.get(), event);
            } else {
                addScenario(hubId, event);
            }
        } catch (RuntimeException e) {
            log.error("❌ Ошибка добавления/обновления сценария '{}' в хабе '{}': {}", name, hubId, e.getMessage());
        }
    }

    private void handleScenarioRemoved(String hubId, ScenarioRemovedEventAvro event) {
        String name = event.getName();
        log.info("🗑 Удаление сценария '{}' из хаба '{}'", name, hubId);
        scenarioRepository.findByHubIdAndName(hubId, name).ifPresent(scenario -> {
            conditionRepository.deleteByScenario(scenario);
            actionRepository.deleteByScenario(scenario);
            scenarioRepository.delete(scenario);
            log.info("✅ Сценарий удалён: {} )", name);
        });
    }

    private void addScenario(String hubId, ScenarioAddedEventAvro event) {
        Scenario scenario = new Scenario();
        scenario.setHubId(hubId);
        scenario.setName(event.getName());
        scenarioRepository.save(scenario);
        addCondition(scenario, event);
        addAction(scenario, event);
        log.info("✅ Сценарий '{}' добавлен в хаб '{}'", scenario.getName(), hubId);
    }

    private void updateScenario(Scenario scenario, ScenarioAddedEventAvro event) {
        actionRepository.deleteByScenario(scenario);
        conditionRepository.deleteByScenario(scenario);
        log.info("♻ Очистка перед обновлением сценария '{}': ",
                scenario.getName());

        addAction(scenario, event);
        addCondition(scenario, event);
        log.info("✅ Сценарий обновлён: '{}' в хабе '{}'", scenario.getName(), scenario.getHubId());
    }

    private void addCondition(Scenario scenario, ScenarioAddedEventAvro event) {
        List<ScenarioConditionAvro> protoList = event.getConditions();
        Map<String, Sensor> sensorMap = getSensorsForScenario(scenario, protoList.stream()
                .map(ScenarioConditionAvro::getSensorId)
                .toList());

        List<Condition> conditions = protoList.stream()
                .map(proto -> mapCondition(scenario, proto, sensorMap.get(proto.getSensorId())))
                .toList();

        conditionRepository.saveAll(conditions);
        log.info("➕ Добавлено {} условий для сценария '{}'", conditions.size(), scenario.getName());
    }

    private void addAction(Scenario scenario, ScenarioAddedEventAvro event) {
        List<DeviceActionAvro> protoList = event.getActions();
        Map<String, Sensor> sensorMap = getSensorsForScenario(scenario, protoList.stream()
                .map(DeviceActionAvro::getSensorId)
                .toList());

        List<Action> actions = protoList.stream()
                .map(proto -> mapAction(scenario, proto, sensorMap.get(proto.getSensorId())))
                .toList();

        actionRepository.saveAll(actions);
        log.info("➕ Добавлено {} действий для сценария '{}'", actions.size(), scenario.getName());
    }

    private Map<String, Sensor> getSensorsForScenario(Scenario scenario, List<String> sensorIds) {
        List<Sensor> sensors = sensorRepository.findAllById(sensorIds);
        boolean valid = sensors.stream()
                .allMatch(sensor -> sensor.getHubId().equals(scenario.getHubId()));

        if (!valid) {
            log.warn("⚠ Обнаружен датчик не из текущего хаба '{}'", scenario.getHubId());
            throw new RuntimeException("Invalid sensor hub ID");
        }

        return sensors.stream()
                .collect(Collectors.toMap(Sensor::getId, Function.identity()));
    }

    private Condition mapCondition(Scenario scenario, ScenarioConditionAvro proto, Sensor sensor) {
        Condition condition = new Condition();
        condition.setScenario(scenario);
        condition.setSensor(sensor);
        condition.setType(ConditionType.valueOf(proto.getType().name()));
        condition.setOperation(ConditionOperation.valueOf(proto.getOperation().name()));

        Object val = proto.getValue();

        if (val instanceof Integer i) {
            condition.setValue(i);
            log.debug("✅ Условие: int value={}, sensorId={}, type={}, operation={}",
                    i, proto.getSensorId(), proto.getType(), proto.getOperation());
        } else if (val instanceof Boolean b) {
            int boolValue = b ? 1 : 0;
            condition.setValue(boolValue);
            log.debug("✅ Условие: bool value={}, sensorId={}, type={}, operation={}",
                    b, proto.getSensorId(), proto.getType(), proto.getOperation());
        } else {
            log.warn("⚠ Неожиданное или отсутствующее значение в условии: sensorId={}, value={}, type={}, operation={}",
                    proto.getSensorId(), val, proto.getType(), proto.getOperation());
            condition.setValue(0); // дефолт
        }

        return condition;
    }
    private Action mapAction(Scenario scenario, DeviceActionAvro proto, Sensor sensor) {
        Action action = new Action();
        action.setScenario(scenario);
        action.setSensor(sensor);
        action.setType(ActionType.valueOf(proto.getType().name()));

        Integer value = proto.getValue();
        if (value != null) {
            action.setValue(value);
        } else {
            log.warn("⚠ Значение действия отсутствует: sensorId='{}', type='{}', сценарий='{}'",
                    proto.getSensorId(), proto.getType(), scenario.getName());
            action.setValue(0); // или не устанавливай вовсе, если логика позволяет
        }

        return action;
    }
}