package ru.practicum.yandex.commerce.interfaceapi.dto.warehose;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseStockDto {

    private UUID productId;
    private long quantity;
}