package ru.practicum.yandex.commerce.interfaceapi.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;
import java.util.UUID;

@FeignClient(name = "warehouse", path = "/api/v1/warehouse")
public interface WarehouseClient {
    @PostMapping("/check")
    Map<UUID, Boolean> checkProducts(@RequestBody Map<UUID, Integer> productList);
}
