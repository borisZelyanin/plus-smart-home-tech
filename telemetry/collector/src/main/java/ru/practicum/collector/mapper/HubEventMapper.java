package ru.practicum.collector.mapper;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.collector.model.hub.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@UtilityClass
public class HubEventMapper {

    public HubEventAvro toAvro(HubEvent event) {
        log.info("🔄 Преобразование события в Avro: {}", event.getClass().getSimpleName());

        if (event instanceof DeviceAddedEvent e) {
            return mapDeviceAddedEvent(e);
        } else if (event instanceof DeviceRemovedEvent e) {
            return mapDeviceRemovedEvent(e);
        } else if (event instanceof ScenarioAddedEvent e) {
            return mapScenarioAddedEvent(e);
        } else if (event instanceof ScenarioRemovedEvent e) {
            return mapScenarioRemovedEvent(e);
        } else {
            throw new IllegalArgumentException("❌ Неизвестный тип события хаба: " + event.getClass());
        }
    }

    private HubEventAvro mapDeviceAddedEvent(DeviceAddedEvent event) {
        log.debug("📦 Маппинг DeviceAddedEvent: {}", event);
        DeviceAddedEventAvro payload = DeviceAddedEventAvro.newBuilder()
                .setId(event.getId())
                .setType(DeviceTypeAvro.valueOf(event.getDeviceType().name()))
                .build();
        return buildHubEventAvro(event.getHubId(), event.getTimestamp(), payload);
    }

    private HubEventAvro mapDeviceRemovedEvent(DeviceRemovedEvent event) {
        log.debug("📦 Маппинг DeviceRemovedEvent: {}", event);
        DeviceRemovedEventAvro payload = DeviceRemovedEventAvro.newBuilder()
                .setId(event.getId())
                .build();
        return buildHubEventAvro(event.getHubId(), event.getTimestamp(), payload);
    }

    private HubEventAvro mapScenarioAddedEvent(ScenarioAddedEvent event) {
        log.debug("📦 Маппинг ScenarioAddedEvent: {}", event);

        List<ScenarioConditionAvro> conditions = event.getConditions().stream()
                .map(c -> {
                    log.trace("🔧 Условие: sensorId={}, type={}, operation={}, value={}",
                            c.getSensorId(), c.getType(), c.getOperation(), c.getValue());
                    return ScenarioConditionAvro.newBuilder()
                            .setSensorId(c.getSensorId())
                            .setType(ConditionTypeAvro.valueOf(c.getType().name()))
                            .setOperation(ConditionOperationAvro.valueOf(c.getOperation().name()))
                            .setValue(c.getValue())
                            .build();
                })
                .collect(Collectors.toList());

        List<DeviceActionAvro> actions = event.getActions().stream()
                .map(a -> {
                    log.trace("⚙️ Действие: sensorId={}, type={}, value={}",
                            a.getSensorId(), a.getType(), a.getValue());
                    return DeviceActionAvro.newBuilder()
                            .setSensorId(a.getSensorId())
                            .setType(ActionTypeAvro.valueOf(a.getType().name()))
                            .setValue(a.getValue())
                            .build();
                })
                .collect(Collectors.toList());

        ScenarioAddedEventAvro payload = ScenarioAddedEventAvro.newBuilder()
                .setName(event.getName())
                .setConditions(conditions)
                .setActions(actions)
                .build();

        return buildHubEventAvro(event.getHubId(), event.getTimestamp(), payload);
    }

    private HubEventAvro mapScenarioRemovedEvent(ScenarioRemovedEvent event) {
        log.debug("📦 Маппинг ScenarioRemovedEvent: {}", event);
        ScenarioRemovedEventAvro payload = ScenarioRemovedEventAvro.newBuilder()
                .setName(event.getName())
                .build();
        return buildHubEventAvro(event.getHubId(), event.getTimestamp(), payload);
    }

    private HubEventAvro buildHubEventAvro(String hubId, Instant timestamp, Object payload) {
        log.debug("🛠 Сборка HubEventAvro: hubId={}, timestamp={}, payloadClass={}",
                hubId, timestamp, payload.getClass().getSimpleName());

        return HubEventAvro.newBuilder()
                .setHubId(hubId)
                .setTimestamp(timestamp != null ? timestamp : Instant.now())
                .setPayload(payload)
                .build();
    }
}