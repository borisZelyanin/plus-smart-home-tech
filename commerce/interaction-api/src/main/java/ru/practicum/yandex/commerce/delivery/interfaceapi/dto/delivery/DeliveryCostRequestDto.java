package ru.practicum.yandex.commerce.delivery.interfaceapi.dto.delivery;

import ru.practicum.yandex.commerce.delivery.interfaceapi.dto.order.OrderDto;

public record DeliveryCostRequestDto(
        OrderDto orderDto,
        String warehouseStreet,
        String deliveryStreet
) {}