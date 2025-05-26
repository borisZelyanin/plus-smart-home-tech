package ru.practicum.analyzer.service;

import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc  ;


@Slf4j
@Component
public class GrpcHubClient {

    private HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    public GrpcHubClient(@GrpcClient("hub-router")
                         HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient) {
        this.hubRouterClient = hubRouterClient;
    }

    /**
     * Отправка действия в gRPC-сервис хаба
     * @param request gRPC-сообщение с действием
     */
    public void sendAction(DeviceActionRequest request) {
        try {
            log.info("📤 Отправка команды в Hub Router: сценарий='{}', сенсор='{}'",
                    request.getScenarioName(), request.getAction().getSensorId());

            hubRouterClient.handleDeviceAction(request);

            log.info("✅ Команда успешно отправлена");
        } catch (Exception e) {
            log.error("❌ Ошибка при отправке команды в Hub Router", e);
        }
    }
}