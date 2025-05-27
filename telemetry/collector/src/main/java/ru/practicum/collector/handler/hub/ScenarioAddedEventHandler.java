package ru.practicum.collector.handler.hub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.collector.model.hub.*;
import ru.practicum.collector.service.HubEventService;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

import java.time.Instant;
import java.util.List;

@Slf4j
@Component
public class ScenarioAddedEventHandler implements HubEventHandler {

    private final HubEventService hubEventService;

    public ScenarioAddedEventHandler(HubEventService hubEventService) {
        this.hubEventService = hubEventService;
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    @Override
    public void handle(HubEventProto event) {
        var payload = event.getScenarioAddedOrBuilder();

        log.info("📥 Обработка события SCENARIO_ADDED от хаба: {}, имя сценария: {}", event.getHubId(), payload.getName());

        // Конвертация условий
        List<ScenarioCondition> conditions = payload.getConditionList().stream()
                .map(proto -> {
                    ScenarioCondition condition = new ScenarioCondition();
                    condition.setSensorId(proto.getSensorId());
                    condition.setType(ConditionType.valueOf(proto.getType().name()));
                    condition.setOperation(ConditionOperation.valueOf(proto.getOperation().name()));

                    switch (proto.getValueCase()) {
                        case BOOL_VALUE -> condition.setValue(proto.getBoolValue());
                        case INT_VALUE -> condition.setValue(proto.getIntValue());
                        default -> log.warn("⚠️ Неизвестный тип значения условия: {}", proto.getValueCase());
                    }

                    log.debug("🔧 Условие: sensorId={}, type={}, operation={}, value={}",
                            condition.getSensorId(), condition.getType(), condition.getOperation(), condition.getValue());

                    return condition;
                })
                .toList();

        // Конвертация действий
        List<DeviceAction> actions = payload.getActionList().stream()
                .map(proto -> {
                    DeviceAction action = new DeviceAction();
                    action.setSensorId(proto.getSensorId());
                    action.setType(ActionType.valueOf(proto.getType().name()));
                    if (proto.hasValue()) {
                        action.setValue(proto.getValue());
                    }

                    log.debug("⚙️ Действие: sensorId={}, type={}, value={}",
                            action.getSensorId(), action.getType(), action.getValue());

                    return action;
                })
                .toList();

        // Сборка финального события
        ScenarioAddedEvent converted = ScenarioAddedEvent.builder()
                .name(payload.getName())
                .conditions(conditions)
                .actions(actions)
                .build();

        converted.setHubId(event.getHubId());
        converted.setTimestamp(Instant.ofEpochSecond(
                event.getTimestamp().getSeconds(),
                event.getTimestamp().getNanos()
        ));

        log.info("📤 Отправка сценария в HubEventService: {}, условий: {}, действий: {}",
                converted.getName(), conditions.size(), actions.size());

        hubEventService.send(converted);

        log.info("✅ Сценарий '{}' от хаба '{}' успешно обработан и отправлен", converted.getName(), converted.getHubId());
    }
}