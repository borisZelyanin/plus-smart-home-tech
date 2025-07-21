package ru.practicum.yandex.commerce.delivery.interfaceapi.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "order", url = "http://order")
public interface OrderClient {

    @PostMapping("/api/v1/order/delivery")
    void markOrderDelivered(@RequestBody UUID orderId);

    @PostMapping("/api/v1/order/delivery/failed")
    void markOrderFailed(@RequestBody UUID orderId);
}