package ru.practicum.yandex.commerce.delivery.interfaceapi.dto.shopping.store.Feign;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.yandex.commerce.delivery.interfaceapi.dto.shopping.store.QuantityState;

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