package ru.practicum.yandex.commerce.delivery.service;

import ru.practicum.yandex.commerce.delivery.interfaceapi.dto.delivery.DeliveryDto;
import ru.practicum.yandex.commerce.delivery.interfaceapi.dto.order.OrderDto;

import java.util.UUID;

public interface DeliveryService {
    DeliveryDto createDelivery(DeliveryDto dto);
    double calculateCost(OrderDto orderDto, String warehouseStreet, String toStreet);
    void markPicked(UUID orderId);
    void markSuccessful(UUID orderId);
    void markFailed(UUID orderId);
}