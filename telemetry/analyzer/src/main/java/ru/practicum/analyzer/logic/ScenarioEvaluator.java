package ru.practicum.analyzer.logic;

import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.analyzer.model.*;
import ru.practicum.analyzer.repository.ActionRepository;
import ru.practicum.analyzer.repository.ConditionRepository;
import ru.practicum.analyzer.service.GrpcHubClient;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScenarioEvaluator {

    private final GrpcHubClient grpcClient;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;

    public void handle(SensorsSnapshotAvro snapshot) {
        String hubId = snapshot.getHubId();
        log.info("📥 Получен снапшот от хаба: {}", hubId);
        log.info("👨‍🦽 Получен снапшот от хаба: {}", snapshot);


        List<Scenario> successfulScenarios = evaluateScenarios(hubId, snapshot);

        if (successfulScenarios.isEmpty()) {
            log.info("📭 Нет сработавших сценариев для хаба: {}", hubId);
            return;
        }

        log.info("✅ Сработавшие сценарии: {}", successfulScenarios.stream().map(Scenario::getName).toList());

        List<Action> actions = actionRepository.findAllByScenarioIn(successfulScenarios);
        for (Action action : actions) {
            grpcClient.sendAction(toRequest(action));
        }

        log.info("📤 😂 Отправлено команд: {}", actions.size());
    }

    private List<Scenario> evaluateScenarios(String hubId, SensorsSnapshotAvro snapshot) {
        Map<String, SensorStateAvro> states = snapshot.getSensorsState();
        List<Condition> allConditions = conditionRepository.findAllByScenarioHubId(hubId);

        Supplier<Stream<SensorEventWrapper>> sensorStream = () ->
                states.entrySet().stream().map(e -> new SensorEventWrapper(e.getKey(), e.getValue()));

        Map<Condition, Boolean> results = new HashMap<>();
        for (Condition condition : allConditions) {
            boolean met = evaluateCondition(sensorStream.get(), condition);
            results.put(condition, met);
            log.debug("📐 Проверка условия [{}]: {} → {}", condition.getId(), condition.getType(), met);
        }

        return results.entrySet().stream()
                .collect(Collectors.groupingBy(
                        entry -> entry.getKey().getScenario(),
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                ))
                .entrySet().stream()
                .filter(entry -> entry.getValue().stream().allMatch(Boolean::booleanValue))
                .map(Map.Entry::getKey)
                .toList();
    }

    private boolean evaluateCondition(Stream<SensorEventWrapper> stream, Condition condition) {
        return stream
                .filter(e -> e.getId().equals(condition.getSensor().getId()))
                .map(extractValue(condition))
                .anyMatch(buildPredicate(condition));
    }

    private Predicate<Integer> buildPredicate(Condition condition) {
        return switch (condition.getOperation()) {
            case EQUALS -> x -> x == condition.getValue();
            case GREATER_THAN -> x -> x > condition.getValue();
            case LOWER_THAN -> x -> x < condition.getValue();
        };
    }

    private Function<SensorEventWrapper, Integer> extractValue(Condition condition) {
        return event -> {
            Object data = event.getData();
            String sensorId = event.getId();

            try {
                return switch (condition.getType()) {
                    case MOTION -> {
                        if (data instanceof MotionSensorAvro motion) {
                            yield motion.getMotion() ? 1 : 0;
                        }
                        log.warn("⚠ Ожидался MotionSensorAvro для сенсора '{}', получено: {}", sensorId, data.getClass().getSimpleName());
                        yield -1;
                    }
                    case LUMINOSITY -> {
                        if (data instanceof LightSensorAvro light) {
                            yield light.getLuminosity();
                        }
                        log.warn("⚠ Ожидался LightSensorAvro для сенсора '{}', получено: {}", sensorId, data.getClass().getSimpleName());
                        yield -1;
                    }
                    case SWITCH -> {
                        if (data instanceof SwitchSensorAvro sw) {
                            yield sw.getState() ? 1 : 0;
                        }
                        log.warn("⚠ Ожидался SwitchSensorAvro для сенсора '{}', получено: {}", sensorId, data.getClass().getSimpleName());
                        yield -1;
                    }
                    case TEMPERATURE -> {
                        if (data instanceof ClimateSensorAvro climate) {
                            yield climate.getTemperatureC();
                        } else if (data instanceof TemperatureSensorAvro temp) {
                            yield temp.getTemperatureC();
                        }
                        log.warn("⚠ Не удалось извлечь TEMPERATURE для сенсора '{}'. Тип данных: {}", sensorId, data.getClass().getSimpleName());
                        yield -1;
                    }
                    case CO2LEVEL -> {
                        if (data instanceof ClimateSensorAvro climate) {
                            yield climate.getCo2Level();
                        }
                        log.warn("⚠ Ожидался ClimateSensorAvro для CO2LEVEL, сенсор '{}', получено: {}", sensorId, data.getClass().getSimpleName());
                        yield -1;
                    }
                    case HUMIDITY -> {
                        if (data instanceof ClimateSensorAvro climate) {
                            yield climate.getHumidity();
                        }
                        log.warn("⚠ Ожидался ClimateSensorAvro для HUMIDITY, сенсор '{}', получено: {}", sensorId, data.getClass().getSimpleName());
                        yield -1;
                    }
                };
            } catch (Exception e) {
                log.warn("⚠ Ошибка извлечения значения из сенсора '{}': {}", sensorId, e.getMessage(), e);
                return -1;
            }
        };
    }

    private DeviceActionRequest toRequest(Action action) {
        Instant now = Instant.now();
        DeviceActionProto proto = DeviceActionProto.newBuilder()
                .setSensorId(action.getSensor().getId())
                .setType(ActionTypeProto.valueOf(action.getType().name()))
                .setValue(action.getValue())
                .build();
        return DeviceActionRequest.newBuilder()
                .setHubId(action.getScenario().getHubId())
                .setScenarioName(action.getScenario().getName())
                .setAction(proto)
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(now.getEpochSecond())
                        .setNanos(now.getNano())
                        .build())
                .build();
    }
}