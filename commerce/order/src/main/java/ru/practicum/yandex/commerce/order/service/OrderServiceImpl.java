package ru.practicum.yandex.commerce.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.yandex.commerce.delivery.interfaceapi.dto.order.OrderDto;
import ru.practicum.yandex.commerce.delivery.interfaceapi.dto.order.OrderState;
import ru.practicum.yandex.commerce.order.mapper.OrderMapper;
import ru.practicum.yandex.commerce.order.model.Order;

import ru.practicum.yandex.commerce.order.repository.OrderRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repository;
    private final OrderMapper mapper;

    @Override
    public Order create(OrderDto dto) {
        Order order = mapper.toEntity(dto);
        order.setState(OrderState.NEW);
        return repository.save(order);
    }

    @Override
    public List<Order> findAllByClient(UUID clientId) {
        return repository.findAllByUsername(clientId.toString()); // username как clientId
    }

    @Override
    public void pay(UUID orderId) {
        updateStatus(orderId, OrderState.PAID);
    }

    @Override
    public void assemble(UUID orderId) {
        updateStatus(orderId, OrderState.ASSEMBLED);
    }

    @Override
    public void ship(UUID orderId) {
        updateStatus(orderId, OrderState.DELIVERED);
    }

    @Override
    public void failPayment(UUID orderId) {
        updateStatus(orderId, OrderState.PAYMENT_FAILED);
    }

    @Override
    public void failAssembly(UUID orderId) {
        updateStatus(orderId, OrderState.ASSEMBLY_FAILED);
    }

    @Override
    public void failDelivery(UUID orderId) {
        updateStatus(orderId, OrderState.DELIVERY_FAILED);
    }

    @Override
    public void returnOrder(UUID orderId) {
        updateStatus(orderId, OrderState.PRODUCT_RETURNED);
    }

    private void updateStatus(UUID orderId, OrderState newState) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        order.setState(newState);
        repository.save(order);
    }
}
