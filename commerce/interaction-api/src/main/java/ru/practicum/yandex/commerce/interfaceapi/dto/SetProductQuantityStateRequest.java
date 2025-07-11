package ru.practicum.yandex.commerce.interfaceapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO для запроса обновления состояния остатков товара.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetProductQuantityStateRequest {

    private UUID productId;

    private QuantityState quantityState;
}