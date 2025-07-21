package ru.practicum.yandex.commerce.delivery.interfaceapi.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.yandex.commerce.delivery.interfaceapi.dto.delivery.DeliveryDto;
import ru.practicum.yandex.commerce.delivery.interfaceapi.dto.order.OrderDto;

@FeignClient(name = "delivery", url = "http://delivery")
public interface DeliveryClient {

    @PostMapping("/api/v1/delivery/cost")
    Double calculateDeliveryCost(@RequestBody OrderDto orderDto);

    @PutMapping("/api/v1/delivery")
    DeliveryDto planDelivery(@RequestBody DeliveryDto dto);
}