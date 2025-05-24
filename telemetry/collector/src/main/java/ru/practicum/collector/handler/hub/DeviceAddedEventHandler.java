package ru.practicum.collector.handler.hub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.collector.model.hub.DeviceAddedEvent;
import ru.practicum.collector.model.hub.DeviceType;
import ru.practicum.collector.service.HubEventService;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

import java.time.Instant;
import java.util.Dictionary;


@Slf4j
@Component
public class DeviceAddedEventHandler implements HubEventHandler {

    private final HubEventService  hubEventService;

    public DeviceAddedEventHandler(HubEventService hubEventService) {
        this.hubEventService = hubEventService;
    }


    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_ADDED;
    }

    @Override
    public void handle(HubEventProto event) {
        var  payload = event.getDeviceAddedOrBuilder();


        DeviceAddedEvent  converted = DeviceAddedEvent.builder()
                .deviceType(DeviceType.valueOf(payload.getType().name()))
                .id(payload.getId())
                .build();

        converted.setHubId(event.getHubId());
        converted.setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()));

        hubEventService.send(converted);

        log.info("📡 Принято событие: Добавлено устройство с id: {}, тип: {}", payload.getId(), payload.getType());
        // логика обработки
    }
}