package ru.practicum.collector.handler.hub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioRemovedEventProtoOrBuilder;

@Slf4j
@Component
public class ScenarioRemovedEventHandler implements HubEventHandler {
    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_REMOVED;
    }

    @Override
    public void handle(HubEventProto event) {
        ScenarioRemovedEventProtoOrBuilder payload = event.getScenarioRemovedOrBuilder();
        log.info("📡 Принято событие: Удалён сценарий: {}", payload.getName());
    }
}