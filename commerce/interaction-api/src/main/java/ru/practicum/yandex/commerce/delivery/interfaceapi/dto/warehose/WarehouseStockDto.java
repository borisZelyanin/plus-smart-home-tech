package ru.practicum.yandex.commerce.delivery.interfaceapi.dto.warehose;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseStockDto {
    private boolean isNew;
    private String message;
    private UUID productId;
    private long quantity;
}