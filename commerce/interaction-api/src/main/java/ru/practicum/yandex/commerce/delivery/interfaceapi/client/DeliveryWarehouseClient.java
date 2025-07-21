package ru.practicum.yandex.commerce.delivery.interfaceapi.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "warehouse", url = "http://warehouse")
public interface DeliveryWarehouseClient {

    @PostMapping("/api/v1/warehouse/send/{orderId}")
    void markShipped(@PathVariable UUID orderId);
}