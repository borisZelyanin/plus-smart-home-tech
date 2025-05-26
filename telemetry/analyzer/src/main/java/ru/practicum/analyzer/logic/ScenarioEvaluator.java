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

    public void handle(SensorsSnapshotAvro snapshot) {
        String hubId = snapshot.getHubId();
        log.info("Handle snapshot from hub ID: \"{}\"", hubId);
        List<Scenario> successfulScenarios = checkScenario(hubId, snapshot);
        if (successfulScenarios.isEmpty()) {
            log.info("Snapshot processing completed from hub ID: \"{}\". No successful scenarios", hubId);
            return;
        }
        log.info("Scenarios worked: {} from hub ID: \"{}\"", successfulScenarios, hubId);
        List<Action> actions = actionRepository.findAllByScenarioIn(successfulScenarios);
        for (Action action : actions) {
            grpcClient.sendAction(mapToActionRequest(action));
        }
        log.info("Actions: {} send to hub ID: \"{}\"", actions, hubId);
    }

    private SensorEventWrapper mapToWrapper(String sensorId, SensorStateAvro sensorStateAvro) {
        SensorEventWrapper wrapper = new SensorEventWrapper();
        wrapper.setId(sensorId);
        wrapper.setData(sensorStateAvro.getData());
        return wrapper;
    }

    private DeviceActionRequest mapToActionRequest(Action action) {
        Scenario scenario = action.getScenario();
        Instant ts = Instant.now();
        Timestamp timestamp = Timestamp.newBuilder()
                .setSeconds(ts.getEpochSecond())
                .setNanos(ts.getNano())
                .build();
        DeviceActionProto deviceActionProto = DeviceActionProto.newBuilder()
                .setSensorId(action.getSensor().getId())
                .setType(ActionTypeProto.valueOf(action.getType().name()))
                .setValue(action.getValue())
                .build();
        return DeviceActionRequest.newBuilder()
                .setHubId(scenario.getHubId())
                .setScenarioName(scenario.getName())
                .setAction(deviceActionProto)
                .setTimestamp(timestamp)
                .build();
    }

    private List<Scenario> checkScenario(String hubId, SensorsSnapshotAvro snapshot) {
        List<Condition> conditions = conditionRepository.findAllByScenarioHubId(hubId);
        Supplier<Stream<SensorEventWrapper>> streamSupplier = () -> snapshot.getSensorsState().entrySet().stream()
                .map(e -> mapToWrapper(e.getKey(), e.getValue()));

        Map<Condition, Boolean> result = new HashMap<>();
        for (Condition condition : conditions) {
            boolean isConditionDone = checkCondition(streamSupplier.get(), condition);
            result.put(condition, isConditionDone);
        }

        Map<Scenario, List<Boolean>> scenarios = result.entrySet().stream()
                .collect(Collectors.groupingBy(e -> e.getKey().getScenario(),
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())));

        return scenarios.entrySet().stream()
                .filter(e -> !e.getValue().contains(false))
                .map(Map.Entry::getKey)
                .toList();
    }

    private boolean checkCondition(Stream<SensorEventWrapper> stream, Condition condition) {
        return stream.filter(o -> o.getId().equals(condition.getSensor().getId()))
                .map(getFunction(condition))
                .anyMatch(getPredicate(condition));
    }

    private Predicate<Integer> getPredicate(Condition condition) {
        ConditionOperation operation = condition.getOperation();
        int value = condition.getValue();
        Predicate<Integer> predicate;

        switch (operation) {
            case EQUALS -> predicate = x -> x == value;
            case GREATER_THAN -> predicate = x -> x > value;
            case LOWER_THAN -> predicate = x -> x < value;
            default -> predicate = null;
        }

        return predicate;
    }

    Function<SensorEventWrapper, Integer> getFunction(Condition condition) {
        ConditionType type = condition.getType();
        Function<SensorEventWrapper, Integer> func;

        switch (type) {
            case MOTION -> func = x -> {
                MotionSensorAvro data = (MotionSensorAvro) x.getData();
                boolean motion = data.getMotion();
                if (motion) {
                    return 1;
                } else {
                    return 0;
                }
            };

            case LUMINOSITY -> func = x -> {
                LightSensorAvro data = (LightSensorAvro) x.getData();
                return data.getLuminosity();
            };

            case SWITCH -> func = x -> {
                SwitchSensorAvro data = (SwitchSensorAvro) x.getData();
                boolean state = data.getState();
                if (state) {
                    return 1;
                } else {
                    return 0;
                }
            };

            case TEMPERATURE -> func = x -> {
                Object object = x.getData();
                if (object instanceof ClimateSensorAvro) {
                    ClimateSensorAvro data = (ClimateSensorAvro) object;
                    return data.getTemperatureC();
                } else {
                    TemperatureSensorAvro data = (TemperatureSensorAvro) object;
                    return data.getTemperatureC();
                }
            };

            case CO2LEVEL -> func = x -> {
                ClimateSensorAvro data = (ClimateSensorAvro) x.getData();
                return data.getCo2Level();
            };

            case HUMIDITY ->
                    func = x -> {
                        ClimateSensorAvro data = (ClimateSensorAvro) x.getData();
                        return data.getHumidity();
                    };
            default -> {
                return null;
            }
        }
        return func;
    }

}