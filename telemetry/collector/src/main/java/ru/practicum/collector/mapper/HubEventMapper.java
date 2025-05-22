package ru.practicum.collector.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.collector.model.hub.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class HubEventMapper {

    public HubEventAvro toAvro(HubEvent event) {
        if (event instanceof DeviceAddedEvent e) {
            return mapDeviceAddedEvent(e);
        } else if (event instanceof DeviceRemovedEvent e) {
            return mapDeviceRemovedEvent(e);
        } else if (event instanceof ScenarioAddedEvent e) {
            return mapScenarioAddedEvent(e);
        } else if (event instanceof ScenarioRemovedEvent e) {
            return mapScenarioRemovedEvent(e);
        } else {
            throw new IllegalArgumentException("Неизвестный тип события хаба: " + event.getClass());
        }
    }

    private HubEventAvro mapDeviceAddedEvent(DeviceAddedEvent event) {
        DeviceAddedEventAvro payload = DeviceAddedEventAvro.newBuilder()
                .setId(event.getId())
                .setType(DeviceTypeAvro.valueOf(event.getDeviceType().name()))
                .build();

        return buildHubEventAvro(event.getHubId(), event.getTimestamp(), payload);
    }

    private HubEventAvro mapDeviceRemovedEvent(DeviceRemovedEvent event) {
        DeviceRemovedEventAvro payload = DeviceRemovedEventAvro.newBuilder()
                .setId(event.getId())
                .build();

        return buildHubEventAvro(event.getHubId(), event.getTimestamp(), payload);
    }

    private HubEventAvro mapScenarioAddedEvent(ScenarioAddedEvent event) {
        List<ScenarioConditionAvro> conditions = event.getConditions().stream()
                .map(c -> ScenarioConditionAvro.newBuilder()
                        .setSensorId(c.getSensorId())
                        .setType(ConditionTypeAvro.valueOf(c.getType().name()))
                        .setOperation(ConditionOperationAvro.valueOf(c.getOperation().name()))
                        .setValue(c.getValue())
                        .build())
                .collect(Collectors.toList());

        List<DeviceActionAvro> actions = event.getActions().stream()
                .map(a -> DeviceActionAvro.newBuilder()
                        .setSensorId(a.getSensorId())
                        .setType(ActionTypeAvro.valueOf(a.getType().name()))
                        .setValue(a.getValue())
                        .build())
                .collect(Collectors.toList());

        ScenarioAddedEventAvro payload = ScenarioAddedEventAvro.newBuilder()
                .setName(event.getName())
                .setConditions(conditions)
                .setActions(actions)
                .build();

        return buildHubEventAvro(event.getHubId(), event.getTimestamp(), payload);
    }

    private HubEventAvro mapScenarioRemovedEvent(ScenarioRemovedEvent event) {
        ScenarioRemovedEventAvro payload = ScenarioRemovedEventAvro.newBuilder()
                .setName(event.getName())
                .build();

        return buildHubEventAvro(event.getHubId(), event.getTimestamp(), payload);
    }

    private HubEventAvro buildHubEventAvro(String hubId, Instant timestamp, Object payload) {
        return HubEventAvro.newBuilder()
                .setHubId(hubId)
                .setTimestamp(timestamp != null ? timestamp.toEpochMilli() : System.currentTimeMillis())
                .setPayload(payload)
                .build();
    }
}
