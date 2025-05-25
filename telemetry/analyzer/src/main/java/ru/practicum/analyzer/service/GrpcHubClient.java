package ru.practicum.analyzer.service;

import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.practicum.analyzer.model.Action;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubRouter.HubRouterControllerGrpc;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@Service
public class GrpcHubClient {

    // gRPC-клиент, внедряется с помощью аннотации @GrpcClient по названию подключения "hub-router"

    private final HubRouterControllerGrpc.HubRouterControllerBlockingStub hubStub;

    public GrpcHubClient( @GrpcClient("hub-router") HubRouterControllerGrpc.HubRouterControllerBlockingStub hubStub) {
        this.hubStub = hubStub;
    }

    /**
     * Метод отправки команды на выполнение действия в хаб
     *
     * @param hubId         идентификатор хаба
     * @param scenarioName  имя сценария, вызвавшего действие
     * @param action        действие, которое необходимо выполнить
     * @param timestamp     момент времени, когда сработал сценарий
     */
    public void sendAction(String hubId, String scenarioName, Action action, Instant timestamp) {
        // Строим сообщение действия
        DeviceActionProto actionProto = buildActionProto(action);

        // Формируем gRPC-запрос
        DeviceActionRequest request = DeviceActionRequest.newBuilder()
                .setHubId(hubId)
                .setScenarioName(scenarioName)
                .setAction(actionProto)
                .setTimestamp(toProtoTimestamp(timestamp))
                .build();

        try {
            // Замер времени отправки (для логгирования производительности)
            Instant start = Instant.now();

            // Отправка команды через gRPC
            hubStub.handleDeviceAction(request);

            // Логгирование успешной отправки
            Duration duration = Duration.between(start, Instant.now());
            log.info("🚀 Команда отправлена: {} -> {}, тип: {}, за {} мс",
                    scenarioName, action.getSensor().getId(), action.getType(), duration.toMillis());
        } catch (Exception e) {
            // Логгирование ошибки при отправке команды
            log.error("❌ Ошибка при отправке gRPC-команды для действия {} -> {}", action.getSensor().getId(), action.getType(), e);
        }
    }

    /**
     * Преобразует объект Action в protobuf-сообщение DeviceActionProto
     */
    private DeviceActionProto buildActionProto(Action action) {
        ActionTypeProto protoType = ActionTypeProto.valueOf(action.getType().name());
        DeviceActionProto.Builder builder = DeviceActionProto.newBuilder()
                .setSensorId(action.getSensor().getId()) // ID устройства
                .setType(protoType); // Тип действия в виде числового значения enum'а

        // Добавляем значение действия, если оно задано (например, целевая температура)
        if (action.getValue() != null) {
            builder.setValue(action.getValue());
        }

        return builder.build();
    }

    /**
     * Преобразует java.time.Instant в protobuf Timestamp
     */
    private Timestamp toProtoTimestamp(Instant instant) {
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }
}