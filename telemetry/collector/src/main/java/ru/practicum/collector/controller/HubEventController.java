package ru.practicum.collector.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.collector.model.hub.*;
import ru.practicum.collector.service.HubEventService;

@RestController
@RequestMapping("/events/hubs")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Hub Events", description = "API для обработки событий от хабов")
public class HubEventController {

    private final HubEventService hubEventService;

    @PostMapping
    @Operation(summary = "Обработка событий от хаба (устройства и сценарии)")
    public void collectHubEvent(@Valid @RequestBody HubEvent event) {
        log.info("Получено событие от хаба: {}", event);

        switch (event.getType()) {
            case DEVICE_ADDED -> hubEventService.send((DeviceAddedEvent) event);
            case DEVICE_REMOVED -> hubEventService.send((DeviceRemovedEvent) event);
            case SCENARIO_ADDED -> hubEventService.send((ScenarioAddedEvent) event);
            case SCENARIO_REMOVED -> hubEventService.send((ScenarioRemovedEvent) event);
            default -> throw new IllegalArgumentException("Неизвестный тип события хаба: " + event.getType());
        }
    }
}
