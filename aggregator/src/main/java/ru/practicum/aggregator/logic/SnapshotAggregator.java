package ru.practicum.aggregator.logic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class SnapshotAggregator {

    private final Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();

    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        String hubId = event.getHubId();
        String sensorId = event.getId();

        // Получаем или создаём снапшот для хаба
        SensorsSnapshotAvro snapshot = snapshots.computeIfAbsent(hubId, id -> {
            SensorsSnapshotAvro newSnapshot = new SensorsSnapshotAvro();
            newSnapshot.setHubId(hubId);
            newSnapshot.setTimestamp(event.getTimestamp());
            newSnapshot.setSensorsState(new HashMap<>());
            return newSnapshot;
        });

        // Получаем текущее состояние для данного сенсора
        Map<String, SensorStateAvro> sensorsState = snapshot.getSensorsState();
        SensorStateAvro currentState = sensorsState.get(sensorId);

        // Если есть текущее состояние, проверим: не старое ли новое событие
        if (currentState != null) {
            long existingMillis = currentState.getTimestamp().toEpochMilli();
            long newMillis = event.getTimestamp().toEpochMilli();

            if (existingMillis > newMillis) {
                return Optional.empty(); // старое событие — игнорируем
            }

            if (currentState.getData().equals(event.getPayload())) {
                return Optional.empty(); // данные не изменились — ничего не делаем
            }
        }

        // Обновляем состояние
        SensorStateAvro newState = new SensorStateAvro();
        newState.setTimestamp(event.getTimestamp());
        newState.setData(event.getPayload());

        sensorsState.put(sensorId, newState);
        snapshot.setTimestamp(event.getTimestamp());

        return Optional.of(snapshot);
    }
}