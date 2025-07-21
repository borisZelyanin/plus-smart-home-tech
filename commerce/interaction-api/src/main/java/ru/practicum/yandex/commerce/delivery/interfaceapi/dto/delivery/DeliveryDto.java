package ru.practicum.yandex.commerce.delivery.interfaceapi.dto.delivery;


import java.util.UUID;

public record DeliveryDto(
        UUID deliveryId,
        AddressDto fromAddress,
        AddressDto toAddress,
        UUID orderId,
        DeliveryState deliveryState
) {}
