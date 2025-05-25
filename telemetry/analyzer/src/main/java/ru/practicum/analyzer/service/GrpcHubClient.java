package ru.practicum.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.practicum.analyzer.model.Action;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import com.google.protobuf.Timestamp;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class GrpcHubClient {

    @GrpcClient("hub-router")
    private final HubRouterControllerGrpc.HubRouterControllerBlockingStub hubStub;

    public void sendAction(String hubId, String scenarioName, Action action, Instant timestamp) {
        DeviceActionProto.Builder actionProto = DeviceActionProto.newBuilder()
                .setSensorId(action.getSensorId())
                .setTypeValue(action.getTypeValue());

        if (action.getValue() != null) {
            actionProto.setValue(action.getValue());
        }

        DeviceActionRequest request = DeviceActionRequest.newBuilder()
                .setHubId(hubId)
                .setScenarioName(scenarioName)
                .setAction(actionProto)
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(timestamp.getEpochSecond())
                        .setNanos(timestamp.getNano())
                        .build())
                .build();

        try {
            hubStub.handleDeviceAction(request);
            log.info("🚀 Команда отправлена: {} -> {}", scenarioName, action.getSensorId());
        } catch (Exception e) {
            log.error("❌ Ошибка при отправке gRPC-команды", e);
        }
    }
}