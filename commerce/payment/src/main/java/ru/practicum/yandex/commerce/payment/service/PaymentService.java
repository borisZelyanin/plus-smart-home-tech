package ru.practicum.yandex.commerce.payment.service;
// PaymentService.java


import ru.practicum.yandex.commerce.delivery.interfaceapi.dto.order.OrderDto;
import ru.practicum.yandex.commerce.delivery.interfaceapi.dto.order.PaymentDto;

import java.util.UUID;

public interface PaymentService {
    Double calculateProductCost(OrderDto orderDto);
    Double calculateTotalCost(OrderDto orderDto);
    PaymentDto createPayment(OrderDto orderDto);
    void markSuccess(UUID paymentId);
    void markFailed(UUID paymentId);
}