package ru.practicum.yandex.commerce.delivery.interfaceapi.dto.order;

import lombok.*;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private UUID orderId;
    private UUID shoppingCartId;
    private Map<UUID, Integer> products;
    private UUID paymentId;
    private UUID deliveryId;
    private OrderState state;
    private Double deliveryWeight;
    private Double deliveryVolume;
    private Boolean fragile;
    private Double totalPrice;
    private Double deliveryPrice;
    private Double productPrice;
}