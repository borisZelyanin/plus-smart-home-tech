package ru.practicum.collector.util;

import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class GrpcRequestLog {

    @Getter
    private final List<String> entries = new ArrayList<>();

    public void log(HubEventProto request) {
        String json;
        try {
            json = JsonFormat.printer().includingDefaultValueFields().print(request);
        } catch (Exception e) {
            json = "[Ошибка преобразования запроса в JSON: %s]".formatted(e.getMessage());
        }

        String summary = """
            -------------------------------
            🛰️ Время: %s
            🧭 Hub ID: %s
            📦 Тип события: %s
            📜 JSON:
            %s
            -------------------------------""".formatted(
                Instant.now(), request.getHubId(), request.getPayloadCase().name(), json
        );

        entries.add(summary);
    }

    // ✅ Новая перегрузка для произвольного запроса
    public void log(String type, String hubId, Message payload) {
        String json;
        try {
            json = JsonFormat.printer().includingDefaultValueFields().print(payload);
        } catch (Exception e) {
            json = "[Ошибка преобразования запроса в JSON: %s]".formatted(e.getMessage());
        }

        String summary = """
            -------------------------------
            🛰️ Время: %s
            🧭 Hub ID: %s
            📦 Тип события: %s
            📜 JSON:
            %s
            -------------------------------""".formatted(
                Instant.now(), hubId, type, json
        );

        entries.add(summary);
    }

    public void printSummary() {
        log.info("\n📋 GRPC ЗАПРОСЫ ПОЛУЧЕННЫЕ COLLECTOR:");
        entries.forEach(log::info);
        log.info("🧾 Всего запросов: {}", entries.size());
    }
}