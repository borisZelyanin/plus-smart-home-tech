package ru.practicum.collector.handler.hub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceAddedEventProtoOrBuilder;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;


@Slf4j
@Component
public class DeviceAddedEventHandler implements HubEventHandler {
    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_ADDED;
    }

    @Override
    public void handle(HubEventProto event) {
        DeviceAddedEventProtoOrBuilder payload = event.getDeviceAddedOrBuilder();
        log.info("📡 Принято событие: Добавлено устройство с id: {}, тип: {}", payload.getId(), payload.getType());
        // логика обработки
    }
}