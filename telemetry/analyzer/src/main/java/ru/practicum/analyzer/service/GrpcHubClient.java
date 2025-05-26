package ru.practicum.analyzer.service;

import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubRouter.HubRouterControllerGrpc;

@Slf4j
@Component
public class GrpcHubClient {
    private final HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    public GrpcHubClient(@GrpcClient("hub-router")
                               HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient) {
        this.hubRouterClient = hubRouterClient;
    }

    public void sendAction(DeviceActionRequest deviceActionRequest) {
        log.info("📤 Отправка команды в HubRouter: {}", deviceActionRequest);
        var response = hubRouterClient.handleDeviceAction(deviceActionRequest);
        log.info("✅ Ответ от HubRouter получен: {}", response);
    }
}