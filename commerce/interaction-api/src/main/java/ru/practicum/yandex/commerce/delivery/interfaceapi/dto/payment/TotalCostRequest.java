package ru.practicum.yandex.commerce.delivery.interfaceapi.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TotalCostRequest {

    private Map<UUID, Integer> products; // productId -> quantity

    private UUID orderId;

    private boolean isFragile;

    private double deliveryWeight;

    private double deliveryVolume;

    private String toStreet;

    private String fromStreet;
}