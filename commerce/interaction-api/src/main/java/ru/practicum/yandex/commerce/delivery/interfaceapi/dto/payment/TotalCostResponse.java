package ru.practicum.yandex.commerce.delivery.interfaceapi.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TotalCostResponse {
    private double totalPrice;
    private double productCost;
    private double deliveryCost;
    private double vat; // налог
}