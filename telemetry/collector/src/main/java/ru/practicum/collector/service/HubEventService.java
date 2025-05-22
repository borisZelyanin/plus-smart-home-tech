package ru.practicum.collector.service;

import ru.practicum.collector.model.hub.DeviceAddedEvent;
import ru.practicum.collector.model.hub.DeviceRemovedEvent;
import ru.practicum.collector.model.hub.ScenarioAddedEvent;
import ru.practicum.collector.model.hub.ScenarioRemovedEvent;

public interface HubEventService {
    void send(DeviceAddedEvent event);

    void send(DeviceRemovedEvent event);

    void send(ScenarioAddedEvent event);

    void send(ScenarioRemovedEvent event);
}
