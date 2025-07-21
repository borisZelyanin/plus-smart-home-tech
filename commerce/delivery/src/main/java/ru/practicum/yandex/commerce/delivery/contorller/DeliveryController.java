package ru.practicum.yandex.commerce.delivery.contorller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.practicum.yandex.commerce.delivery.interfaceapi.dto.delivery.DeliveryDto;
import ru.practicum.yandex.commerce.delivery.interfaceapi.dto.delivery.DeliveryCostRequestDto;
import ru.practicum.yandex.commerce.delivery.service.DeliveryServiceImpl;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/delivery")
@RequiredArgsConstructor
public class DeliveryController {
    private final DeliveryServiceImpl service;

    @PutMapping
    public ResponseEntity<DeliveryDto> planDelivery(@RequestBody DeliveryDto dto) {
        return ResponseEntity.ok(service.createDelivery(dto));
    }

    @PostMapping("/cost")
    public ResponseEntity<Double> getCost(@RequestBody DeliveryCostRequestDto request) {
        return ResponseEntity.ok(service.calculateCost(
                request.orderDto(),
                request.warehouseStreet(),
                request.deliveryStreet()
        ));
    }

    @PostMapping("/picked")
    public ResponseEntity<Void> picked(@RequestBody UUID orderId) {
        service.markPicked(orderId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/successful")
    public ResponseEntity<Void> successful(@RequestBody UUID orderId) {
        service.markSuccessful(orderId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/failed")
    public ResponseEntity<Void> failed(@RequestBody UUID orderId) {
        service.markFailed(orderId);
        return ResponseEntity.ok().build();
    }
}