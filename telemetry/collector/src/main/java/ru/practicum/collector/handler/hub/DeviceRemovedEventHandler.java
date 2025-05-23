package ru.practicum.collector.handler.hub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceRemovedEventProtoOrBuilder;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;


@Slf4j
@Component
public class DeviceRemovedEventHandler implements HubEventHandler {
    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_REMOVED;
    }

    @Override
    public void handle(HubEventProto event) {
        DeviceRemovedEventProtoOrBuilder payload = event.getDeviceRemovedOrBuilder();
        log.info("📡 Принято событие: Удалено устройство с id: {}", payload.getId());
    }
}