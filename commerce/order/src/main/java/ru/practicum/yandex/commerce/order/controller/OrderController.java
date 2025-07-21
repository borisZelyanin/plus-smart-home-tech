package ru.practicum.yandex.commerce.order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.practicum.yandex.commerce.delivery.interfaceapi.dto.order.OrderDto;
import ru.practicum.yandex.commerce.order.mapper.OrderMapper;
import ru.practicum.yandex.commerce.order.model.Order;
import ru.practicum.yandex.commerce.order.service.OrderService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService service;
    private final OrderMapper mapper;

    @PostMapping
    public ResponseEntity<OrderDto> create(@RequestBody OrderDto dto) {
        Order order = service.create(dto);
        return ResponseEntity.ok(mapper.toDto(order));
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<OrderDto>> findAll(@PathVariable UUID clientId) {
        return ResponseEntity.ok(service.findAllByClient(clientId).stream().map(mapper::toDto).toList());
    }

    @PostMapping("/{orderId}/pay")
    public ResponseEntity<Void> pay(@PathVariable UUID orderId) {
        service.pay(orderId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{orderId}/assemble")
    public ResponseEntity<Void> assemble(@PathVariable UUID orderId) {
        service.assemble(orderId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{orderId}/ship")
    public ResponseEntity<Void> ship(@PathVariable UUID orderId) {
        service.ship(orderId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{orderId}/fail/payment")
    public ResponseEntity<Void> paymentFailed(@PathVariable UUID orderId) {
        service.failPayment(orderId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{orderId}/fail/assemble")
    public ResponseEntity<Void> assemblyFailed(@PathVariable UUID orderId) {
        service.failAssembly(orderId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{orderId}/fail/delivery")
    public ResponseEntity<Void> deliveryFailed(@PathVariable UUID orderId) {
        service.failDelivery(orderId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{orderId}/return")
    public ResponseEntity<Void> returnOrder(@PathVariable UUID orderId) {
        service.returnOrder(orderId);
        return ResponseEntity.ok().build();
    }
}