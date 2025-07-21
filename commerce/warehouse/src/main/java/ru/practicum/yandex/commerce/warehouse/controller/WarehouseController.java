package ru.practicum.yandex.commerce.warehouse.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.yandex.commerce.delivery.interfaceapi.dto.warehose.NewProductInWarehouseRequest;
import ru.practicum.yandex.commerce.delivery.interfaceapi.dto.warehose.WarehouseAddressDto;
import ru.practicum.yandex.commerce.delivery.interfaceapi.dto.warehose.WarehouseStockDto;
import ru.practicum.yandex.commerce.warehouse.service.WarehouseService;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService service;

    @GetMapping("/address")
    public WarehouseAddressDto getAllAddresses() {
        return service.getAddress();
    }

    @PutMapping
    public ResponseEntity<Void> addNewProduct(@RequestBody NewProductInWarehouseRequest request) {
        service.addNewProduct(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/add")
    public ResponseEntity<WarehouseStockDto> addStock(@RequestBody WarehouseStockDto request) {
        return ResponseEntity.ok(service.addStock(request));
    }

    @PostMapping("/check")
    public ResponseEntity<Map<UUID, Boolean>> checkProducts(@RequestBody Map<UUID, Integer> productList) {
        Map<UUID, Boolean> result = service.checkProductsAvailability(productList);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/assemble/{orderId}")
    public ResponseEntity<Void> assembleOrder(@PathVariable UUID orderId) {
        service.assembleOrder(orderId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/send-to-delivery/{orderId}")
    public ResponseEntity<Void> sendToDelivery(@PathVariable UUID orderId) {
        service.sendToDelivery(orderId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/return/{orderId}")
    public ResponseEntity<Void> returnOrder(@PathVariable UUID orderId) {
        service.returnOrder(orderId);
        return ResponseEntity.ok().build();
    }
}