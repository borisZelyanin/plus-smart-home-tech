package ru.practicum.yandex.commerce.delivery.interfaceapi.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.yandex.commerce.delivery.interfaceapi.dto.warehose.WarehouseAddressDto;

import java.util.Map;
import java.util.UUID;

@FeignClient(name = "warehouse", path = "/api/v1/warehouse")
public interface WarehouseClient {
    @PostMapping("/check")
    Map<UUID, Boolean> checkProducts(@RequestBody Map<UUID, Integer> productList);

    @PostMapping("/assemble/{orderId}")
    void assembleOrder(@PathVariable UUID orderId);

    @GetMapping("/address")
    WarehouseAddressDto getWarehouseAddress();
}
