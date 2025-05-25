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
        log.info("🚀 Запуск HubEventProcessor...");

        // Подписка на топик
        String topic = props.getHubTopic();
        consumer.subscribe(List.of(topic));
        log.info("✅ Подписка на топик с хаб-событиями: {}", topic);

        try {
            while (true) {
                log.debug("📡 Ожидание новых сообщений из Kafka...");

                ConsumerRecords<String, HubEventAvro> records = consumer.poll(Duration.ofMillis(2000));
                int count = records.count();
                log.debug("📥 Получено {} записей из Kafka", count);

                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    HubEventAvro event = record.value();
                    log.debug("📦 Получен event: {}", record);
                    log.debug("🔍 Обработка события от хаба '{}': ключ='{}', offset={}, partition={}",
                            event.getHubId(), record.key(), record.offset(), record.partition());
                    handleEvent(event);
                }

                log.debug("💾 Асинхронный коммит offset'ов...");
                consumer.commitAsync();
            }
        } catch (WakeupException ignored) {
            log.info("🛑 Завершение обработки событий хаба по сигналу Wakeup");
        } catch (Exception e) {
            log.error("💥 Необработанная ошибка во время обработки событий", e);
        } finally {
            try {
                log.info("💾 Финальный синхронный коммит offset'ов...");
                consumer.commitSync();
            } catch (Exception e) {
                log.warn("⚠ Ошибка при финальном коммите offset'ов", e);
            } finally {
                consumer.close();
                log.info("🧹 Consumer хаба закрыт");
            }
        }
    }

    private void handleEvent(HubEventAvro event) {
        Object payload = event.getPayload();
        String hubId = event.getHubId();

        log.info("📨 Получено событие от хаба: {}, тип payload: {}", hubId, payload.getClass().getSimpleName());

        if (payload instanceof DeviceAddedEventAvro deviceAdded) {
            log.debug("🔧 Обработка события добавления устройства: {}", deviceAdded);
            handleDeviceAdded(hubId, deviceAdded);
        } else if (payload instanceof DeviceRemovedEventAvro deviceRemoved) {
            log.debug("🔧 Обработка события удаления устройства: {}", deviceRemoved);
            handleDeviceRemoved(hubId, deviceRemoved);
        } else if (payload instanceof ScenarioAddedEventAvro scenarioAdded) {
            log.debug("🔧 Обработка события добавления/обновления сценария: {}", scenarioAdded);
            handleScenarioAdded(hubId, scenarioAdded);
        } else if (payload instanceof ScenarioRemovedEventAvro scenarioRemoved) {
            log.debug("🔧 Обработка события удаления сценария: {}", scenarioRemoved);
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
        log.info("📥 Получен сценарий от хаба '{}': {}", hubId, event.getName());

        Optional<Scenario> existing = scenarioRepository.findByHubIdAndName(hubId, event.getName());

        Scenario scenario = existing.orElseGet(() -> {
            Scenario s = new Scenario();
            s.setHubId(hubId);
            s.setName(event.getName());
            return s;
        });

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
                    String sensorId = proto.getSensorId();
                    Sensor sensor = sensorRepository.findByIdAndHubId(sensorId, hubId).orElse(null);
                    if (sensor == null) {
                        log.warn("⚠ Не найден датчик '{}' для условия", sensorId);
                        return null;
                    }
                    Condition condition = new Condition();
                    condition.setSensor(sensor);
                    condition.setScenario(scenario);
                    condition.setType(ConditionType.valueOf(proto.getType().name()));
                    condition.setOperation(ConditionOperation.valueOf(proto.getOperation().name()));

                    Object rawValue = proto.getValue();
                    Integer value = null;

                    if (rawValue instanceof Integer intValue) {
                        value = intValue;
                    } else if (Boolean.TRUE.equals(rawValue)) {
                        value = 1;
                    } else if (Boolean.FALSE.equals(rawValue)) {
                        value = 0;
                    }

                    condition.setValue(value != null ? value : 0);

                    log.debug("✅ Условие добавлено: sensorId={}, type={}, op={}, value={}",
                            sensorId, proto.getType(), proto.getOperation(), condition.getValue());

                    return condition;
                })
                .filter(Objects::nonNull)
                .toList();
        conditionRepository.saveAll(conditions);
        log.info("✅ Сохранено {} условий", conditions.size());

        // Действия
        List<Action> actions = event.getActions().stream()
                .map(proto -> {
                    String sensorId = proto.getSensorId();
                    Sensor sensor = sensorRepository.findByIdAndHubId(sensorId, hubId).orElse(null);
                    if (sensor == null) {
                        log.warn("⚠ Не найден датчик '{}' для действия", sensorId);
                        return null;
                    }
                    Action action = new Action();
                    action.setSensor(sensor);
                    action.setScenario(scenario);
                    action.setType(ActionType.valueOf(proto.getType().name()));
                    action.setValue(proto.getValue());

                    log.debug("✅ Действие добавлено: sensorId={}, type={}, value={}",
                            sensorId, proto.getType(), proto.getValue());

                    return action;
                })
                .filter(Objects::nonNull)
                .toList();
        actionRepository.saveAll(actions);
        log.info("✅ Сохранено {} действий", actions.size());
    }

    private void handleScenarioRemoved(String hubId, ScenarioRemovedEventAvro event) {
        String scenarioName = event.getName();
        log.info("📥 Получен запрос на удаление сценария '{}' от хаба '{}'", scenarioName, hubId);

        scenarioRepository.findByHubIdAndName(hubId, scenarioName).ifPresentOrElse(scenario -> {
            log.debug("🔍 Найден сценарий: id={}, name={}, hubId={}", scenario.getId(), scenario.getName(), scenario.getHubId());

            conditionRepository.deleteByScenario(scenario);
            actionRepository.deleteByScenario(scenario);
            scenarioRepository.delete(scenario);

            log.info("🗑 Удалён сценарий: '{}'", scenario.getName());
        }, () -> {
            log.warn("⚠ Сценарий '{}' для хаба '{}' не найден. Удаление пропущено.", scenarioName, hubId);
        });
    }
}