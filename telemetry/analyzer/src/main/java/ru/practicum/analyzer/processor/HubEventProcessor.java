package ru.practicum.analyzer.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.analyzer.config.AnalyzerProperties;
import ru.practicum.analyzer.model.*;
import ru.practicum.analyzer.repository.ActionRepository;
import ru.practicum.analyzer.repository.ConditionRepository;
import ru.practicum.analyzer.repository.ScenarioRepository;
import ru.practicum.analyzer.repository.SensorRepository;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Duration;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class HubEventProcessor implements Runnable {

    private final Consumer<String, HubEventAvro> consumer;
    private final AnalyzerProperties props;
    private final SensorRepository sensorRepository;
    private final ScenarioRepository scenarioRepository;
    private final ActionRepository actionRepository;
    private final ConditionRepository conditionRepository;

    @Override
    public void run() {
        consumer.subscribe(List.of(props.getHubTopic()));
        log.info("✅ Подписка на топик с хаб-событиями: {}", props.getHubTopic());

        try {
            while (true) {
                ConsumerRecords<String, HubEventAvro> records = consumer.poll(Duration.ofMillis(2000));

                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    HubEventAvro event = record.value();
                    handleEvent(event);
                }

                consumer.commitAsync();
            }
        } catch (WakeupException ignored) {
            log.info("🛑 Завершение обработки событий хаба");
        } catch (Exception e) {
            log.error("💥 Ошибка обработки событий хаба", e);
        } finally {
            try {
                consumer.commitSync();
            } finally {
                consumer.close();
                log.info("🧹 Consumer хаба закрыт");
            }
        }
    }

    private void handleEvent(HubEventAvro event) {
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
            log.warn("⚠ Неизвестный тип события: {}", payload.getClass().getSimpleName());
        }
    }

    private void handleDeviceAdded(String hubId, DeviceAddedEventAvro event) {
        if (!sensorRepository.existsByIdInAndHubId(List.of(event.getId()), hubId)) {
            Sensor sensor = new Sensor();
            sensor.setId(event.getId());
            sensor.setHubId(hubId);
            sensorRepository.save(sensor);
            log.info("➕ Добавлено устройство: {}", event.getId());
        } else {
            log.info("🔁 Устройство уже зарегистрировано: {}", event.getId());
        }
    }

    private void handleDeviceRemoved(String hubId, DeviceRemovedEventAvro event) {
        sensorRepository.findByIdAndHubId(event.getId(), hubId).ifPresent(sensor -> {
            sensorRepository.delete(sensor);
            log.info("➖ Устройство удалено: {}", event.getId());
        });
    }

    private void handleScenarioAdded(String hubId, ScenarioAddedEventAvro event) {
        Optional<Scenario> existing = scenarioRepository.findByHubIdAndName(hubId, event.getName());

        Scenario scenario = existing.orElseGet(() -> {
            Scenario s = new Scenario();
            s.setHubId(hubId);
            s.setName(event.getName());
            return s;
        });

        // Удаляем старые действия и условия, если обновляем сценарий
        if (existing.isPresent()) {
            conditionRepository.deleteByScenario(scenario);
            actionRepository.deleteByScenario(scenario);
            log.info("🔄 Обновление сценария: {}", scenario.getName());
        } else {
            log.info("📝 Добавление нового сценария: {}", scenario.getName());
        }

        scenarioRepository.save(scenario);

        // Условия
        List<Condition> conditions = event.getConditions().stream()
                .map(proto -> {
                    Sensor sensor = sensorRepository.findByIdAndHubId(proto.getSensorId(), hubId).orElse(null);
                    if (sensor == null) {
                        log.warn("⚠ Не найден датчик '{}' для условия", proto.getSensorId());
                        return null;
                    }
                    Condition condition = new Condition();
                    condition.setSensor(sensor);
                    condition.setScenario(scenario);
                    condition.setType(ConditionType.valueOf(proto.getType().name()));
                    condition.setOperation(ConditionOperation.valueOf(proto.getOperation().name()));
                    condition.setValue(
                            proto.getValue() instanceof Integer intVal ? intVal :
                                    Boolean.TRUE.equals(proto.getValue()) ? 1 :
                                            Boolean.FALSE.equals(proto.getValue()) ? 0 : 0
                    );
                    return condition;
                })
                .filter(Objects::nonNull)
                .toList();
        conditionRepository.saveAll(conditions);

        // Действия
        List<Action> actions = event.getActions().stream()
                .map(proto -> {
                    Sensor sensor = sensorRepository.findByIdAndHubId(proto.getSensorId(), hubId).orElse(null);
                    if (sensor == null) {
                        log.warn("⚠ Не найден датчик '{}' для действия", proto.getSensorId());
                        return null;
                    }
                    Action action = new Action();
                    action.setSensor(sensor);
                    action.setScenario(scenario);
                    action.setType(ActionType.valueOf(proto.getType().name()));
                    action.setValue(proto.getValue());
                    return action;
                })
                .filter(Objects::nonNull)
                .toList();
        actionRepository.saveAll(actions);
    }

    private void handleScenarioRemoved(String hubId, ScenarioRemovedEventAvro event) {
        scenarioRepository.findByHubIdAndName(hubId, event.getName()).ifPresent(scenario -> {
            conditionRepository.deleteByScenario(scenario);
            actionRepository.deleteByScenario(scenario);
            scenarioRepository.delete(scenario);
            log.info("🗑 Удалён сценарий: {}", scenario.getName());
        });
    }
}