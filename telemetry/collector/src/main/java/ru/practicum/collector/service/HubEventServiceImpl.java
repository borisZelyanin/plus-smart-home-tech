package ru.practicum.collector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.practicum.collector.mapper.HubEventMapper;
import ru.practicum.collector.model.hub.DeviceAddedEvent;
import ru.practicum.collector.model.hub.DeviceRemovedEvent;
import ru.practicum.collector.model.hub.ScenarioAddedEvent;
import ru.practicum.collector.model.hub.ScenarioRemovedEvent;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

@Slf4j
@Service
@RequiredArgsConstructor
public class HubEventServiceImpl implements HubEventService {

    private final KafkaTemplate<String, HubEventAvro> kafkaTemplate;
    private static final String TOPIC = "telemetry.hubs.v1";

    @Override
    public void send(DeviceAddedEvent event) {
        HubEventAvro avro = HubEventMapper.toAvro(event);
        kafkaTemplate.send(TOPIC, avro.getHubId(), avro);
        log.info("📤 DEVICE_ADDED отправлен: {}", avro);
    }

    @Override
    public void send(DeviceRemovedEvent event) {
        HubEventAvro avro = HubEventMapper.toAvro(event);
        kafkaTemplate.send(TOPIC, avro.getHubId(), avro);
        log.info("📤 DEVICE_REMOVED отправлен: {}", avro);
    }

    @Override
    public void send(ScenarioAddedEvent event) {
        HubEventAvro avro = HubEventMapper.toAvro(event);
        kafkaTemplate.send(TOPIC, avro.getHubId(), avro);
        log.info("📤 SCENARIO_ADDED отправлен: {}", avro);
    }

    @Override
    public void send(ScenarioRemovedEvent event) {
        HubEventAvro avro = HubEventMapper.toAvro(event);
        kafkaTemplate.send(TOPIC, avro.getHubId(), avro);
        log.info("📤 SCENARIO_REMOVED отправлен: {}", avro);
    }
}
