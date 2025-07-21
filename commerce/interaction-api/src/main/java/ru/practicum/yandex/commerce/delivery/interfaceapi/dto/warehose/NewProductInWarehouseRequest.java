package ru.practicum.yandex.commerce.delivery.interfaceapi.dto.warehose;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewProductInWarehouseRequest {
    private UUID productId;
    private boolean fragile;
    private DimensionDto dimension;
    private double weight;
}