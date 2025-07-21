package ru.practicum.yandex.commerce.delivery.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.yandex.commerce.delivery.interfaceapi.dto.delivery.DeliveryDto;
import ru.practicum.yandex.commerce.delivery.interfaceapi.dto.delivery.DeliveryState;
import ru.practicum.yandex.commerce.delivery.mapper.DeliveryMapper;
import ru.practicum.yandex.commerce.delivery.model.Delivery;
import ru.practicum.yandex.commerce.delivery.repository.DeliveryRepository;
import ru.practicum.yandex.commerce.delivery.interfaceapi.dto.order.OrderDto;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {
    private final DeliveryRepository repository;
    private final DeliveryMapper mapper;

    @Override
    public DeliveryDto createDelivery(DeliveryDto dto) {
        Delivery delivery = mapper.toEntity(dto);
        delivery.setDeliveryState(DeliveryState.CREATED);
        return mapper.toDto(repository.save(delivery));
    }

    @Override
    public double calculateCost(OrderDto orderDto, String warehouseStreet, String toStreet) {
        double cost = 5.0;
        if (warehouseStreet.contains("ADDRESS_1")) {
            cost *= 1;
        } else if (warehouseStreet.contains("ADDRESS_2")) {
            cost *= 2;
        }
        cost += 5.0;

        if (Boolean.TRUE.equals(orderDto.getFragile())) {
            cost += cost * 0.2;
        }

        cost += orderDto.getDeliveryWeight() * 0.3;
        cost += orderDto.getDeliveryVolume() * 0.2;

        if (!warehouseStreet.equalsIgnoreCase(toStreet)) {
            cost += cost * 0.2;
        }

        return cost;
    }

    @Override
    public void markPicked(UUID orderId) {
        Delivery delivery = repository.findByOrderId(orderId).orElseThrow();
        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        repository.save(delivery);
    }

    @Override
    public void markSuccessful(UUID orderId) {
        Delivery delivery = repository.findByOrderId(orderId).orElseThrow();
        delivery.setDeliveryState(DeliveryState.DELIVERED);
        repository.save(delivery);
    }

    @Override
    public void markFailed(UUID orderId) {
        Delivery delivery = repository.findByOrderId(orderId).orElseThrow();
        delivery.setDeliveryState(DeliveryState.FAILED);
        repository.save(delivery);
    }
}
