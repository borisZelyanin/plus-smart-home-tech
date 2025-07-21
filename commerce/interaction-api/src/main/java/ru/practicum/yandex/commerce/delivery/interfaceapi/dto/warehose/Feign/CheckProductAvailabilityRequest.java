package ru.practicum.yandex.commerce.delivery.interfaceapi.dto.warehose.Feign;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Запрос на проверку доступности товаров на складе.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckProductAvailabilityRequest {
    private List<UUID> productIds;
}