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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public void evaluate(SensorsSnapshotAvro snapshot) {
        String hubId = snapshot.getHubId();
        log.info("📥 Получен снапшот от хаба: {}", hubId);

        List<Scenario> matchedScenarios = matchScenarios(hubId, snapshot);
        if (matchedScenarios.isEmpty()) {
            log.info("🔍 Ни один сценарий не сработал для хаба: {}", hubId);
            return;
        }

        log.info("✅ Сработавшие сценарии: {}", matchedScenarios.stream()
                .map(Scenario::getName)
                .collect(Collectors.joining(", ")));

        List<Action> actions = actionRepository.findAllByScenarioIn(matchedScenarios);
        for (Action action : actions) {
            grpcClient.sendAction(toGrpcRequest(action));
        }

        log.info("📤 Отправлены {} действий в gRPC для хаба {}", actions.size(), hubId);
    }

    private List<Scenario> matchScenarios(String hubId, SensorsSnapshotAvro snapshot) {
        Map<String, SensorStateAvro> sensorMap = snapshot.getSensorsState();
        List<Condition> allConditions = conditionRepository.findAllByScenarioHubId(hubId);

        Supplier<Stream<SensorEventWrapper>> streamSupplier = () -> sensorMap.entrySet().stream()
                .map(e -> new SensorEventWrapper(e.getKey(), e.getValue().getData()));

        Map<Condition, Boolean> evaluation = new HashMap<>();
        for (Condition condition : allConditions) {
            boolean matched = checkCondition(streamSupplier.get(), condition);
            evaluation.put(condition, matched);
        }

        return evaluation.entrySet().stream()
                .collect(Collectors.groupingBy(e -> e.getKey().getScenario(),
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())))
                .entrySet().stream()
                .filter(e -> e.getValue().stream().allMatch(Boolean::booleanValue))
                .map(Map.Entry::getKey)
                .toList();
    }

    private boolean checkCondition(Stream<SensorEventWrapper> stream, Condition condition) {
        return stream
                .filter(wrapper -> wrapper.getId().equals(condition.getSensor().getId()))
                .map(toSensorValueFunction(condition))
                .anyMatch(toPredicate(condition));
    }

    private Predicate<Integer> toPredicate(Condition condition) {
        return switch (condition.getOperation()) {
            case EQUALS -> val -> val == condition.getValue();
            case GREATER_THAN -> val -> val > condition.getValue();
            case LOWER_THAN -> val -> val < condition.getValue();
        };
    }

    private Function<SensorEventWrapper, Integer> toSensorValueFunction(Condition condition) {
        return wrapper -> {
            Object data = wrapper.getData();

            return switch (condition.getType()) {
                case MOTION -> ((MotionSensorAvro) data).getMotion() ? 1 : 0;
                case LUMINOSITY -> ((LightSensorAvro) data).getLuminosity();
                case SWITCH -> ((SwitchSensorAvro) data).getState() ? 1 : 0;
                case TEMPERATURE -> {
                    if (data instanceof ClimateSensorAvro c) yield c.getTemperatureC();
                    yield ((TemperatureSensorAvro) data).getTemperatureC();
                }
                case CO2LEVEL -> ((ClimateSensorAvro) data).getCo2Level();
                case HUMIDITY -> ((ClimateSensorAvro) data).getHumidity();
            };
        };
    }

    private DeviceActionRequest toGrpcRequest(Action action) {
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