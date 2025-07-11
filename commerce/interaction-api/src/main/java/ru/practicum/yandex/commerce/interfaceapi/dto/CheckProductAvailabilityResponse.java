package ru.practicum.yandex.commerce.interfaceapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

/**
 * Ответ с информацией о доступности товаров.
 * Ключ — UUID товара, значение — доступен ли он.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckProductAvailabilityResponse {
    private Map<UUID, Boolean> availability;
}