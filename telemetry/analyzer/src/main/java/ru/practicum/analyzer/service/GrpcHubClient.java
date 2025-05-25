package ru.practicum.analyzer.service;

import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.practicum.analyzer.model.Action;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubRouter.HubRouterControllerGrpc;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@Component
public class GrpcHubClient {

    private final HubRouterControllerGrpc.HubRouterControllerBlockingStub hubStub;

    public GrpcHubClient(@GrpcClient("hub-router") HubRouterControllerGrpc.HubRouterControllerBlockingStub hubStub) {
        this.hubStub = hubStub;
    }

    public void sendAction(String hubId, String scenarioName, Action action, Instant timestamp) {
        DeviceActionProto actionProto = buildActionProto(action);

        DeviceActionRequest request = DeviceActionRequest.newBuilder()
                .setHubId(hubId)
                .setScenarioName(scenarioName)
                .setAction(actionProto)
                .setTimestamp(toProtoTimestamp(timestamp))
                .build();

        try {
            Instant start = Instant.now();
            log.debug("📤 Отправка команды: hubId={}, scenario='{}', sensorId={}, type={}, value={}",
                    hubId, scenarioName, action.getSensor().getId(), action.getType(), action.getValue());

            hubStub.handleDeviceAction(request);

            Duration duration = Duration.between(start, Instant.now());
            log.info("🚀 Команда успешно отправлена: {} -> {}, тип: {}, за {} мс",
                    scenarioName, action.getSensor().getId(), action.getType(), duration.toMillis());
        } catch (Exception e) {
            log.error("❌ Ошибка при отправке gRPC-команды: hubId={}, sensorId={}, type={}",
                    hubId, action.getSensor().getId(), action.getType(), e);
        }
    }

    private DeviceActionProto buildActionProto(Action action) {
        ActionTypeProto protoType = ActionTypeProto.valueOf(action.getType().name());
        DeviceActionProto.Builder builder = DeviceActionProto.newBuilder()
                .setSensorId(action.getSensor().getId())
                .setType(protoType);

        if (action.getValue() != null) {
            builder.setValue(action.getValue());
        }

        return builder.build();
    }

    private Timestamp toProtoTimestamp(Instant instant) {
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }
}