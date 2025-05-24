package ru.practicum.collector.controller.grpc;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import ru.practicum.collector.handler.sensor.SensorEventHandler;
import ru.practicum.collector.handler.hub.HubEventHandler;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@GrpcService
public class EventController extends CollectorControllerGrpc.CollectorControllerImplBase {

    private final Map<SensorEventProto.PayloadCase, SensorEventHandler> sensorHandlers;
    private final Map<HubEventProto.PayloadCase, HubEventHandler> hubHandlers;

    public EventController(Set<SensorEventHandler> sensorHandlers,
                           Set<HubEventHandler> hubHandlers) {
        this.sensorHandlers = sensorHandlers.stream()
                .collect(Collectors.toMap(SensorEventHandler::getMessageType, Function.identity()));

        this.hubHandlers = hubHandlers.stream()
                .collect(Collectors.toMap(HubEventHandler::getMessageType, Function.identity()));
    }

    @Override
    public void collectSensorEvent(SensorEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            SensorEventProto.PayloadCase payloadType = request.getPayloadCase();

            if (!sensorHandlers.containsKey(payloadType)) {
                throw new IllegalArgumentException("❌ Неизвестный тип сенсорного события: " + payloadType);
            }

            sensorHandlers.get(payloadType).handle(request);

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL.withDescription(e.getMessage()).withCause(e)
            ));
        }
    }

    @Override
    public void collectHubEvent(HubEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            HubEventProto.PayloadCase payloadType = request.getPayloadCase();

            if (!hubHandlers.containsKey(payloadType)) {
                throw new IllegalArgumentException("❌ Неизвестный тип события хаба: " + payloadType);
            }

            hubHandlers.get(payloadType).handle(request);

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL.withDescription(e.getMessage()).withCause(e)
            ));
        }
    }
}
