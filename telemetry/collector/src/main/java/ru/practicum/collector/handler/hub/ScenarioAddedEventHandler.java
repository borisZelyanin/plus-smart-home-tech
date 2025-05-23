package ru.practicum.collector.handler.hub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProtoOrBuilder;


@Slf4j
@Component
public class ScenarioAddedEventHandler implements HubEventHandler {
    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    @Override
    public void handle(HubEventProto event) {
        ScenarioAddedEventProtoOrBuilder payload = event.getScenarioAddedOrBuilder();
        log.info("📡 Принято событие: Добавлен сценарий: {}", payload.getName());
    }
}