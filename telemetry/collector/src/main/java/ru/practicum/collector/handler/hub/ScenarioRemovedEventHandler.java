package ru.practicum.collector.handler.hub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.collector.model.hub.ScenarioRemovedEvent;
import ru.practicum.collector.service.HubEventService;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

import java.time.Instant;

@Slf4j
@Component
public class ScenarioRemovedEventHandler implements HubEventHandler {

    private final HubEventService hubEventService;

    public ScenarioRemovedEventHandler(HubEventService hubEventService) {
        this.hubEventService = hubEventService;
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_REMOVED;
    }

    @Override
    public void handle(HubEventProto event) {
        var payload = event.getScenarioRemovedOrBuilder();

        ScenarioRemovedEvent converted = ScenarioRemovedEvent.builder()
                .name(payload.getName())
                .build();

        converted.setHubId(event.getHubId());
        converted.setTimestamp(Instant.ofEpochSecond(
                event.getTimestamp().getSeconds(),
                event.getTimestamp().getNanos()
        ));

        hubEventService.send(converted);

        log.info("📡 Принято событие: Удалён сценарий: {}", payload.getName());
    }
}