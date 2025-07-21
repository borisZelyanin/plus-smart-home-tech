package ru.practicum.yandex.commerce.order.service;


import ru.practicum.yandex.commerce.delivery.interfaceapi.dto.order.OrderDto;
import ru.practicum.yandex.commerce.order.model.Order;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    Order create(OrderDto dto);
    List<Order> findAllByClient(UUID clientId);
    void pay(UUID orderId);
    void assemble(UUID orderId);
    void ship(UUID orderId);
    void failPayment(UUID orderId);
    void failAssembly(UUID orderId);
    void failDelivery(UUID orderId);
    void returnOrder(UUID orderId);
}
