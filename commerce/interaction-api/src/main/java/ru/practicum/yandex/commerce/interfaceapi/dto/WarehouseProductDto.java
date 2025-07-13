package ru.practicum.yandex.commerce.interfaceapi.dto;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseProductDto {
    private UUID productId;
    private double width;
    private double height;
    private double depth;
    private double weight;
    private boolean fragile;
}